import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;

public class Database {

    private static String DB_HOST;
    private static String DB_PORT;
    private static String DB_NAME;
    private static String DB_USER;
    private static String DB_PASS;

    static {
        try {
            Properties props = new Properties();
            FileInputStream fis = new FileInputStream("data.env"); 
            props.load(fis);
            DB_HOST = props.getProperty("DB_HOST");
            DB_PORT = props.getProperty("DB_PORT");
            DB_NAME = props.getProperty("DB_NAME");
            DB_USER = props.getProperty("DB_USER");
            DB_PASS = props.getProperty("DB_PASS");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur : impossible de charger le fichier .env");
        }
    }

    public static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME + "?useSSL=false&serverTimezone=UTC";
        return DriverManager.getConnection(url, DB_USER, DB_PASS);
    }


    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            System.out.println("Connexion OK !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


