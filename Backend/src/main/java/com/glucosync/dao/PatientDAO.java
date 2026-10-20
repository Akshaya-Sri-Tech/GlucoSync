package com.glucosync.dao;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class PatientDAO {

    // Represents a patient record from the database
    public static class PatientRecord {

        private long patientId;
        private String name;
        private Integer age;
        private String diabetesType;
        private BigDecimal minGlucose;
        private BigDecimal maxGlucose;

        public PatientRecord(long patientId, String name,
                             Integer age, String diabetesType,
                             BigDecimal minGlucose,
                             BigDecimal maxGlucose) {
            this.patientId = patientId;
            this.name = name;
            this.age = age;
            this.diabetesType = diabetesType;
            this.minGlucose = minGlucose;
            this.maxGlucose = maxGlucose;
        }

        public long getPatientId() {
            return patientId;
        }

        public String getName() {
            return name;
        }

        public Integer getAge() {
            return age;
        }

        public String getDiabetesType() {
            return diabetesType;
        }

        public BigDecimal getMinGlucose() {
            return minGlucose;
        }

        public BigDecimal getMaxGlucose() {
            return maxGlucose;
        }

        @Override
        public String toString() {
            return "Patient ID: " + patientId
                    + ", Name: " + name
                    + ", Age: " + age
                    + ", Diabetes Type: " + diabetesType
                    + ", Minimum Glucose: " + minGlucose
                    + ", Maximum Glucose: " + maxGlucose;
        }
    }

    // 1. Retrieve all patients
    public List<PatientRecord> getAllPatients()
            throws SQLException {

        String sql = """
                SELECT patient_id, name, age, diabetes_type,
                       min_glucose, max_glucose
                FROM patients
                ORDER BY patient_id
                """;

        List<PatientRecord> patients = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                patients.add(mapPatient(rs));
            }
        }

        return patients;
    }

    // 2. Retrieve a patient by ID
    public Optional<PatientRecord> getPatientById(long patientId)
            throws SQLException {

        String sql = """
                SELECT patient_id, name, age, diabetes_type,
                       min_glucose, max_glucose
                FROM patients
                WHERE patient_id = ?
                """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, patientId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapPatient(rs));
                }
            }
        }

        return Optional.empty();
    }

    // 3. Add a new patient
    public long addPatient(String name, Integer age,
                           String diabetesType,
                           BigDecimal minGlucose,
                           BigDecimal maxGlucose)
            throws SQLException {

        validatePatient(name, age, diabetesType,
                        minGlucose, maxGlucose);

        String sql = """
                INSERT INTO patients
                    (name, age, diabetes_type,
                     min_glucose, max_glucose)
                VALUES (?, ?, ?, ?, ?)
                RETURNING patient_id
                """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name.trim());

            if (age == null) {
                ps.setNull(2, java.sql.Types.INTEGER);
            } else {
                ps.setInt(2, age);
            }

            ps.setString(3, diabetesType);

            ps.setBigDecimal(4, minGlucose);
            ps.setBigDecimal(5, maxGlucose);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("patient_id");
                }
            }
        }

        throw new SQLException("Patient insertion failed.");
    }

    // 4. Update an existing patient
    public boolean updatePatient(long patientId, String name,
                                 Integer age, String diabetesType,
                                 BigDecimal minGlucose,
                                 BigDecimal maxGlucose)
            throws SQLException {

        validatePatient(name, age, diabetesType,
                        minGlucose, maxGlucose);

        String sql = """
                UPDATE patients
                SET name = ?,
                    age = ?,
                    diabetes_type = ?,
                    min_glucose = ?,
                    max_glucose = ?
                WHERE patient_id = ?
                """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name.trim());

            if (age == null) {
                ps.setNull(2, java.sql.Types.INTEGER);
            } else {
                ps.setInt(2, age);
            }

            ps.setString(3, diabetesType);
            ps.setBigDecimal(4, minGlucose);
            ps.setBigDecimal(5, maxGlucose);
            ps.setLong(6, patientId);

            return ps.executeUpdate() > 0;
        }
    }

   

    // Maps a database row to a PatientRecord
    private PatientRecord mapPatient(ResultSet rs)
            throws SQLException {

        int ageValue = rs.getInt("age");
        Integer age = rs.wasNull() ? null : ageValue;

        return new PatientRecord(
                rs.getLong("patient_id"),
                rs.getString("name"),
                age,
                rs.getString("diabetes_type"),
                rs.getBigDecimal("min_glucose"),
                rs.getBigDecimal("max_glucose")
        );
    }

    // Validates patient data before insert/update
    private void validatePatient(String name, Integer age,
                                 String diabetesType,
                                 BigDecimal minGlucose,
                                 BigDecimal maxGlucose) {

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Patient name cannot be empty.");
        }

        if (age != null && age <= 0) {
            throw new IllegalArgumentException(
                    "Age must be greater than zero.");
        }

        if (diabetesType == null ||
                diabetesType.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Diabetes type cannot be empty.");
        }

        if (minGlucose != null && maxGlucose != null
                && minGlucose.compareTo(maxGlucose) >= 0) {
            throw new IllegalArgumentException(
                    "Minimum glucose must be less than maximum glucose.");
        }
    }

    // Simple test: retrieve and display all patients
    public static void main(String[] args) {

        PatientDAO dao = new PatientDAO();

        try {
            List<PatientRecord> patients = dao.getAllPatients();

            for (PatientRecord patient : patients) {
                System.out.println(patient);
            }

        } catch (SQLException e) {
            System.err.println(
                    "Error retrieving patients: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
