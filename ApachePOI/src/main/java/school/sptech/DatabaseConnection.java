package school.sptech;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe responsável por gerenciar a conexão com o banco de dados MySQL.
 */
public class DatabaseConnection {

    private static final String HOST = "localhost";
    private static final String PORT = "3306";
    private static final String DATABASE = "simecom";
    private static final String USER = "root";
    private static final String PASSWORD = "123@Simecom123";

    private static final String URL = String.format(
            "jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Sao_Paulo&rewriteBatchedStatements=true",
            HOST, PORT, DATABASE
    );

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("[ERRO] Falha ao fechar conexão: " + e.getMessage());
            }
        }
    }

    /**
     * Evita o warning do thread mysql-cj-abandoned-connection-cleanup
     * ao encerrar a aplicação pelo Maven exec:java.
     */
    public static void shutdownMySqlCleanupThread() {
        try {
            Class<?> cleanupThreadClass = Class.forName("com.mysql.cj.jdbc.AbandonedConnectionCleanupThread");
            cleanupThreadClass.getMethod("checkedShutdown").invoke(null);
        } catch (ClassNotFoundException ignored) {
            // Driver não carregado ainda.
        } catch (Exception e) {
            System.err.println("[AVISO] Não foi possível encerrar o cleanup thread do MySQL: " + e.getMessage());
        }
    }
}