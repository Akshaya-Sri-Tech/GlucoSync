import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PatientDAO {

    public void getAllPatients() {

        String sql = """
                SELECT patient_id, name, age, diabetes_type,
                       min_glucose, max_glucose
                FROM Patients
                """;

        try (
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement =
                    connection.prepareStatement(sql);
            ResultSet result = statement.executeQuery()
        ) {
            System.out.println("\n===== GLUCOSYNC PATIENTS =====");

            boolean found = false;

            while (result.next()) {
                found = true;

                System.out.println("Patient ID: "
                        + result.getInt("patient_id"));
                System.out.println("Name: "
                        + result.getString("name"));
                System.out.println("Age: "
                        + result.getInt("age"));
                System.out.println("Diabetes Type: "
                        + result.getString("diabetes_type"));
                System.out.println("Minimum Glucose: "
                        + result.getBigDecimal("min_glucose"));
                System.out.println("Maximum Glucose: "
                        + result.getBigDecimal("max_glucose"));

                System.out.println("-----------------------------");
            }

            if (!found) {
                System.out.println("No patients found.");
            }

        } catch (SQLException e) {
            System.out.println("Failed to fetch patients.");
            System.out.println("Reason: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        PatientDAO patientDAO = new PatientDAO();
        patientDAO.getAllPatients();
    }
}
