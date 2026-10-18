
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/GlucoSync";

    public static Connection getConnection() throws SQLException {
        String user = System.getenv("GLUCOSYNC_DB_USER");
        String password = System.getenv("GLUCOSYNC_DB_PASSWORD");

        if (user == null || password == null) {
            throw new SQLException(
                "Set GLUCOSYNC_DB_USER and GLUCOSYNC_DB_PASSWORD first."
            );
        }

        return DriverManager.getConnection(URL, user, password);
    }

    public static void main(String[] args) {
        try (Connection connection = getConnection()) {
            System.out.println("SUCCESS: Connected to GlucoSync!");
            System.out.println("Database: " + connection.getCatalog());
        } catch (SQLException e) {
            System.out.println("FAILED: Could not connect to MySQL.");
            System.out.println("Reason: " + e.getMessage());
        }
    }
}
