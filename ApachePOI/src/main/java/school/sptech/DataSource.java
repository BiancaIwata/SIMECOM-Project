package school.sptech;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataSource {
        private static final String URL = String.format(
                "jdbc:mysql://localhost:3306/simecom",
                Config.DB_HOST,
                Config.DB_PORT,
                Config.DB_NAME
        );

        public static Connection getConnection() throws SQLException {
            return DriverManager.getConnection(URL, Config.DB_USER, Config.DB_PASSWORD);
        }
}
