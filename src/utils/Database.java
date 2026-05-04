package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Centralise la création des connexions JDBC.
 */
public final class Database {

    private static final String URL = Config.get(
            "DB_URL",
            "jdbc:mysql://localhost:3306/billeterie?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
    );
    private static final String USER = Config.get("DB_USER", "root");
    private static final String PASSWORD = Config.get("DB_PASSWORD", "");

    private Database() {
    }

    // Ouvre une connexion JDBC selon les variables d'environnement.
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
