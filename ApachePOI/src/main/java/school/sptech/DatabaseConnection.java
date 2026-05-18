package school.sptech;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe responsável por gerenciar a conexão com o banco de dados MySQL.
 * Lê configurações da classe Config (variáveis de ambiente).
 */
public class DatabaseConnection {

    // URL da conexão JDBC com configurações recomendadas para performance
    private static final String URL = String.format(
            "jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Sao_Paulo&rewriteBatchedStatements=true",
            Config.DB_HOST, Config.DB_PORT, Config.DB_NAME
    );

    // ABRIR UMA NOVA CONEXÃO
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, Config.DB_USER, Config.DB_PASSWORD);
    }

    // FECHAR CONEXÃO DE FORMA SEGURA
    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("[ERRO] Falha ao fechar conexão: " + e.getMessage());
            }
        }
    }
}
