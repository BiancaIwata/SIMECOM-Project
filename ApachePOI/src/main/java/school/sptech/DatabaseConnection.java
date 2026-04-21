package school.sptech;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe responsável por gerenciar a conexão com o banco de dados MySQL.
 * Configurações padrão apontam para o banco 'simecom' local.
 */
public class DatabaseConnection {

    // CONFIGURAÇÃO DO BANCO
    private static final String HOST = "localhost";
    private static final String PORT = "3306";
    private static final String DATABASE = "simecom";
    private static final String USER = "root";
    private static final String PASSWORD = "123@Simecom123"; 

    // Caminho da conexão JDBC por lotes e configurações recomendadas para performance
    private static final String URL = String.format(
            "jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Sao_Paulo&rewriteBatchedStatements=true",
            HOST, PORT, DATABASE
    );

    // ABRIR UMA NOVA CONEXÃO
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
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
