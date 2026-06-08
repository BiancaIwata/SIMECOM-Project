package school.sptech;

import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Config {

    // ════════════════════════════════════════════════════════════════
    // BANCO DE DADOS
    // ════════════════════════════════════════════════════════════════

    public static final String DB_HOST = getEnvOrDefault("DB_HOST", "32.198.149.171");
    public static final String DB_PORT = getEnvOrDefault("DB_PORT", "3306");
    public static final String DB_NAME = getEnvOrDefault("DB_NAME", "simecom");
    public static final String DB_USER = getEnvOrDefault("DB_USER", "root");
    public static final String DB_PASSWORD = getEnvOrDefault("DB_PASSWORD", "123@Simecom123");


    // ════════════════════════════════════════════════════════════════
    // AWS S3
    // ════════════════════════════════════════════════════════════════

    public static final String S3_BUCKET_NAME = getEnvOrDefault("S3_BUCKET_NAME", "simecom-bucket-s3");
    public static final String S3_REGION = getEnvOrDefault("AWS_REGION", "us-east-1");
    public static final String S3_PREFIX = getEnvOrDefault("S3_PREFIX", "01-raw/");
    public static final String LOG_LEVEL = getEnvOrDefault("LOG_LEVEL", "INFO");

    // ════════════════════════════════════════════════════════════════

    /**
     * Lê variável de ambiente obrigatória.
     * Lança exceção se não estiver definida.
     */
    private static String getEnvRequired(String key) {
        String value = System.getenv(key);
        if (value == null || value.trim().isEmpty()) {
            throw new RuntimeException(String.format(
                    "[ERRO] Variável de ambiente obrigatória não está definida: %s%n" +
                            "Configure em .env ou nas variáveis do sistema.%n" +
                            "Veja .env.example para detalhes.", key));
        }
        return value;
    }

    /**
     * Lê variável de ambiente com valor padrão.
     */
    private static String getEnvOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value != null && !value.trim().isEmpty()) ? value : defaultValue;
    }

    /**
     * Exibe as configurações atuais (sem senhas).
     */
    public static void exibirConfiguracao() {
        System.out.println("\n╔══════════════════════════════════════════════════════╗");
        System.out.println("║         CONFIGURAÇÃO SIMECOM - Data Loader           ║");
        System.out.println("╠══════════════════════════════════════════════════════╣");
        System.out.printf("║ Banco de Dados: %s:%s/%s%n", DB_HOST, DB_PORT, DB_NAME);
        System.out.printf("║ Usuário BD: %s%n", DB_USER);
        System.out.printf("║ S3 Bucket: %s (região: %s)%n", S3_BUCKET_NAME, S3_REGION);
        System.out.printf("║ S3 Prefixo: %s%n", S3_PREFIX);
        System.out.printf("║ Log Level: %s%n", LOG_LEVEL);
        System.out.println("╚══════════════════════════════════════════════════════╝\n");
    }

    private static final Properties props = new Properties();

    static {
        try (InputStream in = Config.class
                .getClassLoader()
                .getResourceAsStream("application.properties")) {
            props.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar configurações", e);
        }
    }

    public static String get(String key) {
        return props.getProperty(key);
    }
}
