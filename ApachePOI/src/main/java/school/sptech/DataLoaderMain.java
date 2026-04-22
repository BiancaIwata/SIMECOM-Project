package school.sptech;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * =====================================================================
 *  SIMECOM - Data Loader S3 (Comércio Exterior - MDIC)
 * =====================================================================
 */
public class DataLoaderMain {

    private static final String S3_PREFIX = System.getenv().getOrDefault("S3_PREFIX", "01-raw/");
    private static final Path TEMP_DIR = Paths.get(System.getProperty("java.io.tmpdir"), "simecom-s3");

    public static void main(String[] args) {
        System.out.println("###################################################");
        System.out.println("#   SIMECOM - Data Loader S3 (Comércio Exterior)  #");
        System.out.println("#   Fonte: AWS S3 <- MDIC / ComexStat            #");
        System.out.println("###################################################");
        System.out.println();

        String arquivoAlvo = (args.length > 0) ? args[0].trim() : "";

        try {
            if (arquivoAlvo.isEmpty()) {
                modoListar();
            } else if (arquivoAlvo.equalsIgnoreCase("todos")) {
                modoTodos();
            } else {
                modoUnico(arquivoAlvo);
            }
        } finally {
            DatabaseConnection.shutdownMySqlCleanupThread();
        }
    }

    private static void modoListar() {
        System.out.println("[MODO] Listando arquivos disponíveis no bucket.\n");
        System.out.println("  Para processar UM arquivo:");
        System.out.println("    mvn exec:java -Dexec.mainClass=\"school.sptech.DataLoaderMain\" -Dexec.args=\"NOME_DO_ARQUIVO.xlsx\"\n");
        System.out.println("  Para processar TODOS:");
        System.out.println("    mvn exec:java -Dexec.mainClass=\"school.sptech.DataLoaderMain\" -Dexec.args=\"todos\"\n");

        S3Service s3 = null;
        try {
            s3 = new S3Service();
            List<String> arquivos = s3.listarArquivos(S3_PREFIX);

            if (arquivos.isEmpty()) {
                System.out.println("[AVISO] Nenhum arquivo encontrado no bucket.");
                return;
            }

            System.out.println("\n[S3] Arquivos encontrados:");
            for (String key : arquivos) {
                String nome = extrairNomeArquivo(key).toUpperCase();
                String tipo;
                if (nome.contains("TABELAS_AUXILIARES")) tipo = "AUXILIAR";
                else if (nome.startsWith("EXP_")) tipo = "EXPORTACAO";
                else if (nome.startsWith("IMP_")) tipo = "IMPORTACAO";
                else tipo = "DESCONHECIDO";

                System.out.printf("  %-40s [%s]%n", extrairNomeArquivo(key), tipo);
            }
            System.out.printf("\n  Total: %d arquivos%n", arquivos.size());

        } catch (Exception e) {
            System.err.println("[ERRO] " + e.getMessage());
        } finally {
            if (s3 != null) s3.close();
        }
    }

    private static void modoUnico(String nomeArquivo) {
        System.out.printf("[MODO] Processando arquivo único: %s%n%n", nomeArquivo);

        long startTime = System.currentTimeMillis();
        S3Service s3 = null;
        Connection conn = null;

        try {
            s3 = new S3Service();

            List<String> todosArquivos = s3.listarArquivos(S3_PREFIX);
            String keyEncontrada = null;

            for (String key : todosArquivos) {
                if (extrairNomeArquivo(key).equalsIgnoreCase(nomeArquivo)) {
                    keyEncontrada = key;
                    break;
                }
            }

            if (keyEncontrada == null) {
                System.err.printf("[ERRO] Arquivo '%s' não encontrado no bucket!%n", nomeArquivo);
                System.out.println("[INFO] Use o modo sem argumentos para ver os arquivos disponíveis.");
                return;
            }

            System.out.println("[DB] Conectando ao banco simecom...");
            conn = DatabaseConnection.getConnection();
            System.out.println("[DB] Conexão estabelecida!\n");

            String fileName = extrairNomeArquivo(keyEncontrada).toUpperCase();
            logJava log = new logJava(extrairNomeArquivo(keyEncontrada));

            if (fileName.contains("TABELAS_AUXILIARES")) {
                System.out.println("=== Processando: TABELAS AUXILIARES ===\n");
                System.out.println("[AVISO] TabelasAuxiliaresLoader foi removido do projeto.");
                System.out.println("[INFO] Os códigos SH4/País serão criados automaticamente pelos EXP/IMP.\n");
                inserirSetoresPadrao(conn);
                log.sucesso(1, 0);

            } else if (fileName.startsWith("EXP_")) {
                System.out.println("=== Processando: EXPORTAÇÃO ===\n");
                inserirSetoresPadrao(conn);
                Path local = downloadS3(s3, keyEncontrada);
                ComexDataLoader.carregarExportacao(local, conn, log);

            } else if (fileName.startsWith("IMP_")) {
                System.out.println("=== Processando: IMPORTAÇÃO ===\n");
                inserirSetoresPadrao(conn);
                Path local = downloadS3(s3, keyEncontrada);
                ComexDataLoader.carregarImportacao(local, conn, log);

            } else {
                System.err.printf("[AVISO] Arquivo '%s' não é reconhecido (não é AUXILIAR, EXP_ ou IMP_).%n", nomeArquivo);
                return;
            }

            log.salvarNoBanco(conn);

        } catch (Exception e) {
            System.err.println("\n[ERRO FATAL] " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConnection.close(conn);
            if (s3 != null) s3.close();
        }

        long elapsed = System.currentTimeMillis() - startTime;
        System.out.printf("\n[FIM] Arquivo processado em %.1f segundos.%n", elapsed / 1000.0);
    }

    private static void modoTodos() {
        System.out.println("[MODO] Processando TODOS os arquivos do bucket.\n");

        long startTime = System.currentTimeMillis();
        int arquivosProcessados = 0;
        int arquivosComErro = 0;

        S3Service s3 = null;
        Connection conn = null;

        try {
            s3 = new S3Service();
            List<String> todosArquivos = s3.listarArquivos(S3_PREFIX);

            if (todosArquivos.isEmpty()) {
                System.out.println("[AVISO] Nenhum arquivo encontrado no bucket.");
                return;
            }

            List<String> auxiliaresXlsx = new ArrayList<>();
            List<String> exportacoes = new ArrayList<>();
            List<String> importacoes = new ArrayList<>();

            for (String key : todosArquivos) {
                String fileName = extrairNomeArquivo(key).toUpperCase();
                if (fileName.endsWith(".XLSX")) {
                    if (fileName.contains("TABELAS_AUXILIARES")) auxiliaresXlsx.add(key);
                    else if (fileName.startsWith("EXP_")) exportacoes.add(key);
                    else if (fileName.startsWith("IMP_")) importacoes.add(key);
                }
            }

            System.out.printf("[S3] Auxiliares: %d | Exportações: %d | Importações: %d%n%n",
                    auxiliaresXlsx.size(), exportacoes.size(), importacoes.size());

            conn = DatabaseConnection.getConnection();
            System.out.println("[DB] Conexão estabelecida!\n");

            if (!auxiliaresXlsx.isEmpty()) {
                String key = auxiliaresXlsx.get(0);
                logJava logAux = new logJava(extrairNomeArquivo(key));
                try {
                    System.out.println("[AVISO] TabelasAuxiliaresLoader removido. Pulando arquivos auxiliares.");
                    logAux.sucesso(1, 0);
                    arquivosProcessados++;
                } catch (Exception e) {
                    System.err.printf("[ERRO] %s: %s%n", key, e.getMessage());
                    logAux.erro(0, e.getMessage());
                    arquivosComErro++;
                }
                logAux.salvarNoBanco(conn);
                logAux.imprimirResumo();
            }

            for (String key : exportacoes) {
                logJava log = new logJava(extrairNomeArquivo(key));
                try {
                    Path local = downloadS3(s3, key);
                    ComexDataLoader.carregarExportacao(local, conn, log);
                    log.salvarNoBanco(conn);
                    arquivosProcessados++;
                } catch (Exception e) {
                    System.err.printf("[ERRO] %s: %s%n", key, e.getMessage());
                    log.erro(0, e.getMessage());
                    log.salvarNoBanco(conn);
                    arquivosComErro++;
                }
            }

            for (String key : importacoes) {
                logJava log = new logJava(extrairNomeArquivo(key));
                try {
                    Path local = downloadS3(s3, key);
                    ComexDataLoader.carregarImportacao(local, conn, log);
                    log.salvarNoBanco(conn);
                    arquivosProcessados++;
                } catch (Exception e) {
                    System.err.printf("[ERRO] %s: %s%n", key, e.getMessage());
                    log.erro(0, e.getMessage());
                    log.salvarNoBanco(conn);
                    arquivosComErro++;
                }
            }

        } catch (Exception e) {
            System.err.println("\n[ERRO FATAL] " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConnection.close(conn);
            if (s3 != null) s3.close();
        }

        long elapsed = System.currentTimeMillis() - startTime;
        System.out.println();
        System.out.println("####################################################");
        System.out.println("#            RESUMO DO PIPELINE S3                 #");
        System.out.println("####################################################");
        System.out.printf("#  Arquivos processados  : %-23d #\n", arquivosProcessados);
        System.out.printf("#  Arquivos com erro     : %-23d #\n", arquivosComErro);
        System.out.printf("#  Tempo total           : %-19.1f seg #\n", elapsed / 1000.0);
        System.out.println("####################################################");
        System.out.println("\n[FIM] Pipeline S3 encerrado.");
    }

    private static Path downloadS3(S3Service s3, String key) throws Exception {
        String fileName = extrairNomeArquivo(key);
        Path localPath = TEMP_DIR.resolve(fileName);
        return s3.download(key, localPath);
    }

    private static void inserirSetoresPadrao(Connection conn) throws Exception {
        System.out.println("[SETORES] Inserindo setores padrão...");

        String[] setores = {
                "Animais vivos e produtos do reino animal",
                "Produtos do reino vegetal",
                "Gorduras e óleos animais ou vegetais",
                "Produtos das indústrias alimentares; bebidas e tabaco",
                "Produtos minerais",
                "Produtos das indústrias químicas",
                "Plásticos e borracha",
                "Peles, couros e obras",
                "Madeira, carvão vegetal e cortiça",
                "Pasta de madeira, papel e cartão",
                "Matérias têxteis e suas obras",
                "Calçados, chapéus e semelhantes",
                "Obras de pedra, cerâmica e vidro",
                "Pérolas, pedras preciosas e metais preciosos",
                "Metais comuns e suas obras",
                "Máquinas e aparelhos, material elétrico",
                "Material de transporte",
                "Instrumentos e aparelhos de óptica, fotografia e cinematografia",
                "Armas e munições",
                "Mercadorias e produtos diversos",
                "Objetos de arte e antiguidades"
        };

        String sql = "INSERT IGNORE INTO setores (nome) VALUES (?)";
        try (var ps = conn.prepareStatement(sql)) {
            for (String setor : setores) {
                ps.setString(1, setor);
                ps.addBatch();
            }
            ps.executeBatch();
        }
        System.out.println("[SETORES] Pronto.\n");
    }

    private static String extrairNomeArquivo(String key) {
        int lastSlash = key.lastIndexOf('/');
        return lastSlash >= 0 ? key.substring(lastSlash + 1) : key;
    }
}