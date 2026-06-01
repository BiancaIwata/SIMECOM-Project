package school.sptech;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class DataLoaderMain {

    private static final String S3_PREFIX = Config.S3_PREFIX;
    private static final Path TEMP_DIR = Paths.get(System.getProperty("java.io.tmpdir"), "simecom-bucket-s3");

    public static void main(String[] args) {
        Config.exibirConfiguracao();

        String arquivoAlvo = (args.length > 0) ? args[0].trim() : "";

        if (arquivoAlvo.isEmpty()) {
            modoListar();
        } else if (arquivoAlvo.equalsIgnoreCase("todos")) {
            modoTodos();
        } else {
            modoUnico(arquivoAlvo);
        }
    }

    private static void modoListar() {
        try (S3Service s3 = new S3Service()) {
            List<String> arquivos = s3.listarArquivos(S3_PREFIX);

            if (arquivos.isEmpty()) {
                System.out.println("[AVISO] Nenhum arquivo encontrado no bucket.");
                return;
            }

            for (String key : arquivos) {
                String nome = extrairNomeArquivo(key).toUpperCase();
                String tipo = classificarTipo(nome);
                System.out.printf("%s [%s]%n", extrairNomeArquivo(key), tipo);
            }

        } catch (Exception e) {
            System.err.println("[ERRO] " + e.getMessage());
        }
    }

    private static void modoUnico(String nomeArquivo) {
        long startTime = System.currentTimeMillis();

        try (S3Service s3 = new S3Service()) {
            List<String> todosArquivos = s3.listarArquivos(S3_PREFIX);
            String keyEncontrada = localizarArquivo(todosArquivos, nomeArquivo);

            if (keyEncontrada == null) {
                System.err.printf("[ERRO] Arquivo '%s' não encontrado no bucket!%n", nomeArquivo);
                return;
            }

            Connection conn = DatabaseConnection.getConnection();
            String fileName = extrairNomeArquivo(keyEncontrada).toUpperCase();

            try (ProcessorContext ctx = new ProcessorContext(conn, s3, S3_PREFIX)) {
                processarArquivo(ctx, keyEncontrada, fileName);
            } catch (Exception e) {
                System.err.println("[ERRO FATAL] " + e.getMessage());
                e.printStackTrace();
            } finally {
                DatabaseConnection.close(conn);
            }

            printarTempo(startTime);

        } catch (Exception e) {
            System.err.println("[ERRO] " + e.getMessage());
        }
    }

    private static void modoTodos() {
        long startTime = System.currentTimeMillis();
        int arquivosProcessados = 0;
        int arquivosComErro = 0;

        try (S3Service s3 = new S3Service()) {
            List<String> todosArquivos = s3.listarArquivos(S3_PREFIX);

            if (todosArquivos.isEmpty()) {
                System.out.println("[AVISO] Nenhum arquivo encontrado no bucket.");
                return;
            }

            List<String> exportacoes = new ArrayList<>();
            List<String> importacoes = new ArrayList<>();

            for (String key : todosArquivos) {
                String fileName = extrairNomeArquivo(key).toUpperCase();
                if (fileName.endsWith(".XLSX")) {
                    if (fileName.startsWith("EXP_")) exportacoes.add(key);
                    else if (fileName.startsWith("IMP_")) importacoes.add(key);
                }
            }

            Connection conn = DatabaseConnection.getConnection();

            try (ProcessorContext ctx = new ProcessorContext(conn, s3, S3_PREFIX)) {
                for (String key : exportacoes) {
                    try {
                        processarArquivo(ctx, key, extrairNomeArquivo(key).toUpperCase());
                        arquivosProcessados++;
                    } catch (Exception e) {
                        System.err.printf("[ERRO] %s: %s%n", key, e.getMessage());
                        arquivosComErro++;
                    }
                }

                for (String key : importacoes) {
                    try {
                        processarArquivo(ctx, key, extrairNomeArquivo(key).toUpperCase());
                        arquivosProcessados++;
                    } catch (Exception e) {
                        System.err.printf("[ERRO] %s: %s%n", key, e.getMessage());
                        arquivosComErro++;
                    }
                }
            } catch (Exception e) {
                System.err.println("[ERRO FATAL] " + e.getMessage());
                e.printStackTrace();
            } finally {
                DatabaseConnection.close(conn);
            }

            System.out.printf("[PIPELINE] Arquivos processados: %d | Erros: %d | Tempo: %.1f seg%n",
                    arquivosProcessados, arquivosComErro, (System.currentTimeMillis() - startTime) / 1000.0);

        } catch (Exception e) {
            System.err.println("[ERRO] " + e.getMessage());
        }
    }

    private static void processarArquivo(ProcessorContext ctx, String key, String fileName)
            throws Exception {
        Path local = baixarDoS3(ctx.getS3Service(), key);

        DataProcessor processor;
        if (fileName.contains("TABELAS_AUXILIARES") || fileName.contains("UF_MUN2") || fileName.contains("PAIS")) {
            processor = new TabelasAuxiliaresProcessor(ctx);
        } else if (fileName.startsWith("EXP_")) {
            processor = new ExportacaoProcessor(ctx, extrairNomeArquivo(key));
        } else if (fileName.startsWith("IMP_")) {
            processor = new ImportacaoProcessor(ctx, extrairNomeArquivo(key));
        } else {
            System.err.printf("[AVISO] Arquivo '%s' não é reconhecido.\n", fileName);
            return;
        }

        ProcessingResult resultado = processor.processar(local);
        if (!resultado.isSucesso()) {
            System.err.printf("[ERRO] Processamento falhou: %s%n", resultado.getMensagem());
        }
    }

    private static String localizarArquivo(List<String> arquivos, String nomeArquivo) {
        for (String key : arquivos) {
            if (extrairNomeArquivo(key).equalsIgnoreCase(nomeArquivo)) {
                return key;
            }
        }
        return null;
    }

    private static String classificarTipo(String nomeArquivo) {
        if (nomeArquivo.startsWith("EXP_")) return "EXPORTACAO";
        if (nomeArquivo.startsWith("IMP_")) return "IMPORTACAO";
        if (nomeArquivo.contains("PAIS")) return "TABELA_AUXILIAR";
        return "DESCONHECIDO";
    }

    private static Path baixarDoS3(S3Service s3, String key) throws Exception {
        String fileName = extrairNomeArquivo(key);
        Path localPath = TEMP_DIR.resolve(fileName);
        return s3.download(key, localPath);
    }

    private static String extrairNomeArquivo(String key) {
        int lastSlash = key.lastIndexOf('/');
        return lastSlash >= 0 ? key.substring(lastSlash + 1) : key;
    }

    private static void printarTempo(long startTime) {
        long elapsed = System.currentTimeMillis() - startTime;
        System.out.printf("[FIM] Concluído em %.1f segundos%n", elapsed / 1000.0);
    }
}