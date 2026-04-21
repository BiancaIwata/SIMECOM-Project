package school.sptech;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 *  SIMECOM - Data Loader S3 (Comércio Exterior - MDIC)
 *
 *  Pipeline automático de carga de dados do MDIC a partir de um bucket
 *  AWS S3, inserindo diretamente no banco de dados MySQL 'simecom'.
 *
 *  Fluxo:
 *    1) Conecta ao bucket S3 configurado
 *    2) Lista todos os arquivos XLSX disponíveis
 *    3) Classifica automaticamente por tipo (auxiliar, exportação, importação)
 *    4) Baixa cada arquivo e processa via Apache POI
 *    5) Insere os dados no banco MySQL com batch insert
 *    6) Salva o log de cada arquivo na tabela log_java
 *
 *  Arquivos XLSX esperados no bucket:
 *    - TABELAS_AUXILIARES.xlsx → Tabelas de referência (SH4, países, municípios)
 *    - EXP_*.xlsx              → Dados de exportação por ano
 *    - IMP_*.xlsx              → Dados de importação por ano
 *
 *  Configuração (variáveis de ambiente):
 *    - S3_BUCKET_NAME        → nome do bucket (padrão: simecom-s3)
 *    - S3_PREFIX             → prefixo/pasta no bucket (padrão: vazio = raiz)
 *    - AWS_REGION            → região AWS (padrão: us-east-1)
 *    - AWS_ACCESS_KEY_ID     → chave de acesso
 *    - AWS_SECRET_ACCESS_KEY → chave secreta
 *
 *  Pré-requisitos:
 *    - Bucket S3 com os arquivos XLSX de dados do MDIC
 *    - MySQL rodando com o banco 'simecom' criado (execute script.sql antes)
 *    - Credenciais AWS configuradas
 *    - mvn clean compile exec:java
 */
public class DataLoaderMain {

    /** Prefixo (pasta) no bucket S3 — configurável via variável de ambiente */
    private static final String S3_PREFIX = System.getenv().getOrDefault("S3_PREFIX", "01-raw/");

    /** Diretório local temporário para arquivos baixados do S3 */
    private static final Path TEMP_DIR = Paths.get(System.getProperty("java.io.tmpdir"), "simecom-s3");

    public static void main(String[] args) {
        System.out.println("###################################################");
        System.out.println("#   SIMECOM - Data Loader S3 (Comércio Exterior)  #");
        System.out.println("#   Fonte: AWS S3 ← MDIC / ComexStat              #");
        System.out.println("###################################################");
        System.out.println();

        long startTime = System.currentTimeMillis();

        int arquivosProcessados = 0;
        int arquivosComErro = 0;
        int totalArquivosEncontrados = 0;

        S3Service s3 = null;
        Connection conn = null;

        try {
            //  ETAPA 1: Conectar ao S3 e listar arquivos
            System.out.println("=== ETAPA 1/5: Conectando ao S3 e listando arquivos ===\n");

            s3 = new S3Service();
            List<String> todosArquivos = s3.listarArquivos(S3_PREFIX);
            totalArquivosEncontrados = todosArquivos.size();

            if (todosArquivos.isEmpty()) {
                System.out.println("[AVISO] Nenhum arquivo encontrado no bucket.");
                return;
            }

            // ► Classificar arquivos XLSX automaticamente pelo nome
            List<String> auxiliaresXlsx = new ArrayList<>();
            List<String> exportacoes    = new ArrayList<>();
            List<String> importacoes    = new ArrayList<>();
            List<String> desconhecidos  = new ArrayList<>();

            for (String key : todosArquivos) {
                String fileName = extrairNomeArquivo(key).toUpperCase();

                if (fileName.endsWith(".XLSX")) {
                    if (fileName.contains("TABELAS_AUXILIARES")) {
                        auxiliaresXlsx.add(key);
                    } else if (fileName.startsWith("EXP_")) {
                        exportacoes.add(key);
                    } else if (fileName.startsWith("IMP_")) {
                        importacoes.add(key);
                    } else {
                        desconhecidos.add(key);
                    }
                } else {
                    desconhecidos.add(key);
                }
            }

            System.out.printf("[S3] Classificação dos arquivos no bucket '%s':%n", s3.getBucketName());
            System.out.printf("  Auxiliares XLSX      : %d%n", auxiliaresXlsx.size());
            System.out.printf("  Exportação (XLSX)    : %d%n", exportacoes.size());
            System.out.printf("  Importação (XLSX)    : %d%n", importacoes.size());
            if (!desconhecidos.isEmpty()) {
                System.out.printf("  Outros (ignorados)   : %d  →  %s%n", desconhecidos.size(), desconhecidos);
            }
            System.out.println();

            //  ETAPA 2: Conectar ao banco de dados
            System.out.println("=== ETAPA 2/5: Conectando ao banco de dados ===\n");

            System.out.println("[DB] Testando conexão com o banco simecom...");
            conn = DatabaseConnection.getConnection();
            System.out.println("[DB] Conexão estabelecida com sucesso!\n");

            //  ETAPA 3: Tabelas auxiliares (setores + SH4/país/mun)
            System.out.println("=== ETAPA 3/5: Carregando tabelas auxiliares ===\n");

            inserirSetoresPadrao(conn);

            if (!auxiliaresXlsx.isEmpty()) {
                String key = auxiliaresXlsx.get(0);
                System.out.printf("[AUX] Processando XLSX: %s%n", key);
                logJava logAux = new logJava(extrairNomeArquivo(key));
                try {
                    Path local = downloadS3(s3, key);
                    TabelasAuxiliaresLoader.carregarTudo(local, conn);
                    logAux.sucesso(1, 0);
                    arquivosProcessados++;
                } catch (Exception e) {
                    System.err.printf("[ERRO] Falha ao processar %s: %s%n", key, e.getMessage());
                    logAux.erro(0, e.getMessage());
                    arquivosComErro++;
                }
                logAux.salvarNoBanco(conn);
                logAux.imprimirResumo();
            } else {
                System.out.println("[AVISO] TABELAS_AUXILIARES.xlsx não encontrado no bucket!");
                System.out.println("        As tabelas de referência não serão carregadas.");
            }

            //  ETAPA 4: Dados de Exportação (XLSX)
            System.out.println("\n=== ETAPA 4/5: Carregando exportações ===\n");

            if (exportacoes.isEmpty()) {
                System.out.println("[INFO] Nenhum arquivo de exportação encontrado no bucket.");
            }
            for (String key : exportacoes) {
                logJava log = new logJava(extrairNomeArquivo(key));
                try {
                    Path local = downloadS3(s3, key);
                    ComexDataLoader.carregarExportacao(local, conn);
                    log.sucesso(log.getLinhasInseridas(), log.getLinhasIgnoradas());
                    arquivosProcessados++;
                } catch (Exception e) {
                    System.err.printf("[ERRO] %s: %s%n", key, e.getMessage());
                    log.erro(0, e.getMessage());
                    arquivosComErro++;
                }
                log.salvarNoBanco(conn);
                log.imprimirResumo();
            }

            //  ETAPA 5: Dados de Importação (XLSX)
            System.out.println("\n=== ETAPA 5/5: Carregando importações ===\n");

            if (importacoes.isEmpty()) {
                System.out.println("[INFO] Nenhum arquivo de importação encontrado no bucket.");
            }
            for (String key : importacoes) {
                logJava log = new logJava(extrairNomeArquivo(key));
                try {
                    Path local = downloadS3(s3, key);
                    ComexDataLoader.carregarImportacao(local, conn);
                    log.sucesso(log.getLinhasInseridas(), log.getLinhasIgnoradas());
                    arquivosProcessados++;
                } catch (Exception e) {
                    System.err.printf("[ERRO] %s: %s%n", key, e.getMessage());
                    log.erro(0, e.getMessage());
                    arquivosComErro++;
                }
                log.salvarNoBanco(conn);
                log.imprimirResumo();
            }

        } catch (Exception e) {
            System.err.println("\n[ERRO FATAL] " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConnection.close(conn);
            if (s3 != null) s3.close();
        }

        //  RESUMO FINAL
        long elapsed = System.currentTimeMillis() - startTime;
        String bucketName = System.getenv().getOrDefault("S3_BUCKET_NAME", "simecom-s3");

        System.out.println();
        System.out.println("####################################################");
        System.out.println("#            RESUMO DO PIPELINE S3                 #");
        System.out.println("####################################################");
        System.out.printf("#  Bucket                : %-23s #\n", bucketName);
        System.out.printf("#  Arquivos encontrados  : %-23d #\n", totalArquivosEncontrados);
        System.out.printf("#  Arquivos processados  : %-23d #\n", arquivosProcessados);
        System.out.printf("#  Arquivos com erro     : %-23d #\n", arquivosComErro);
        System.out.printf("#  Tempo total           : %-19.1f seg #\n", elapsed / 1000.0);
        System.out.println("####################################################");

        System.out.println("\n[FIM] Pipeline S3 encerrado.");
    }

    //  DOWNLOAD S3 → DIRETÓRIO TEMPORÁRIO LOCAL
    private static Path downloadS3(S3Service s3, String key) throws Exception {
        String fileName = extrairNomeArquivo(key);
        Path localPath = TEMP_DIR.resolve(fileName);
        return s3.download(key, localPath);
    }

    //  INSERÇÃO DE SETORES PADRÃO
    private static void inserirSetoresPadrao(Connection conn) throws Exception {
        System.out.println("[SETORES] Inserindo setores padrão de comércio exterior...");

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
        System.out.println("[SETORES] 21 setores padrão inseridos.\n");
    }

    //  UTILITÁRIOS
    private static String extrairNomeArquivo(String key) {
        int lastSlash = key.lastIndexOf('/');
        return lastSlash >= 0 ? key.substring(lastSlash + 1) : key;
    }
}
