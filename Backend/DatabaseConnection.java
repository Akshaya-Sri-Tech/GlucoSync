
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.nio.file.Files;
import java.nio.file.Path;

import io.github.cdimascio.dotenv.Dotenv;

public class DatabaseConnection {

    private static final String URL =
            "jdbc:postgresql://aws-0-ap-northeast-2.pooler.supabase.com:5432/postgres?sslmode=require";

    private static final Dotenv dotenv = loadDotenv();

    private static Dotenv loadDotenv() {
        Path workingDir = Path.of("").toAbsolutePath().normalize();

        Path[] candidates = {
            workingDir,
            workingDir.resolve("Backend"),
            workingDir.resolve("../Backend").normalize()
        };

        for (Path directory : candidates) {
            if (Files.isRegularFile(directory.resolve(".env"))) {
                return Dotenv.configure()
                        .directory(directory.toString())
                        .ignoreIfMissing()
                        .load();
            }
        }

        // Also supports environment variables without a .env file.
        return Dotenv.configure()
                .directory(workingDir.toString())
                .ignoreIfMissing()
                .load();
    }

    private static String getConfig(String key) {
        String value = System.getenv(key);

        if (value == null || value.isBlank()) {
            value = dotenv.get(key);
        }

        return value;
    }

    public static Connection getConnection() throws SQLException {
        String user = getConfig("GLUCOSYNC_DB_USER");
        String password = getConfig("GLUCOSYNC_DB_PASSWORD");

        if (user == null || user.isBlank()
                || password == null || password.isBlank()) {
            throw new SQLException(
                    "Missing GLUCOSYNC_DB_USER or GLUCOSYNC_DB_PASSWORD. "
                    + "Set environment variables or configure Backend/.env."
            );
        }

        return DriverManager.getConnection(URL, user, password);
    }

    public static void main(String[] args) {
        try (Connection connection = getConnection()) {
            System.out.println("SUCCESS: Connected to GlucoSync Supabase!");
            System.out.println("Database: " + connection.getCatalog());
            System.out.println("Database type: "
                    + connection.getMetaData().getDatabaseProductName());
        } catch (SQLException e) {
            System.err.println("FAILED: Could not connect to Supabase.");
            System.err.println("Reason: " + e.getMessage());
        }
    }
}
