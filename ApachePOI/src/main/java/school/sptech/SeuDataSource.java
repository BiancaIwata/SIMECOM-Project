package school.sptech;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SeuDataSource {
        private static final String URL = String.format(
                "jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=UTC",
                Config.DB_HOST,
                Config.DB_PORT,
                Config.DB_NAME
        );

        public static Connection getConnection() throws SQLException {
            return DriverManager.getConnection(URL, Config.DB_USER, Config.DB_PASSWORD);
        }
}
