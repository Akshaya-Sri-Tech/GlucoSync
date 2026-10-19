
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import io.github.cdimascio.dotenv.Dotenv;

public class DatabaseConnection {

    private static final Dotenv dotenv = Dotenv.configure()
        .directory(".")
        .ignoreIfMissing()
        .load();
    private static final String URL =
            "jdbc:postgresql://aws-0-ap-northeast-2.pooler.supabase.com:5432/postgres?sslmode=require";

    public static Connection getConnection() throws SQLException {
        String user = dotenv.get("GLUCOSYNC_DB_USER");
        String password = dotenv.get("GLUCOSYNC_DB_PASSWORD");

        if (user == null || password == null) {
            throw new SQLException(
                    "Set GLUCOSYNC_DB_USER and GLUCOSYNC_DB_PASSWORD first."
            );
        }

        return DriverManager.getConnection(URL, user, password);
    }

    public static void main(String[] args) {
        try (Connection connection = getConnection()) {
            System.out.println("SUCCESS: Connected to GlucoSync Supabase!");
            System.out.println("Database: " + connection.getCatalog());
            System.out.println("Database type: PostgreSQL");
        } catch (SQLException e) {
            System.out.println("FAILED: Could not connect to Supabase.");
            System.out.println("Reason: " + e.getMessage());
        }
    }
}
