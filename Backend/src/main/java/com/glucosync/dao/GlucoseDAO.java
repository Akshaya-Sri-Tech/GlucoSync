package com.glucosync.dao;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class GlucoseDAO {

    // Represents one row from glucose_readings
    public static class GlucoseReadingRecord {

        private long readingId;
        private long patientId;
        private BigDecimal glucoseValue;
        private OffsetDateTime readingTime;
        private String status;
        private String mealContext;

        public GlucoseReadingRecord(
                long readingId,
                long patientId,
                BigDecimal glucoseValue,
                OffsetDateTime readingTime,
                String status,
                String mealContext) {

            this.readingId = readingId;
            this.patientId = patientId;
            this.glucoseValue = glucoseValue;
            this.readingTime = readingTime;
            this.status = status;
            this.mealContext = mealContext;
        }

        public long getReadingId() {
            return readingId;
        }

        public long getPatientId() {
            return patientId;
        }

        public BigDecimal getGlucoseValue() {
            return glucoseValue;
        }

        public OffsetDateTime getReadingTime() {
            return readingTime;
        }

        public String getStatus() {
            return status;
        }

        public String getMealContext() {
            return mealContext;
        }

        @Override
        public String toString() {
            return "Reading ID: " + readingId
                    + ", Patient ID: " + patientId
                    + ", Glucose Value: " + glucoseValue
                    + ", Reading Time: " + readingTime
                    + ", Status: " + status
                    + ", Meal Context: " + mealContext;
        }
    }

    // 1. Retrieve the latest three readings for a patient
    public List<GlucoseReadingRecord> getLastThreeReadings(
            long patientId) throws SQLException {

        String sql = """
                SELECT reading_id, patient_id, glucose_value,
                       reading_time, status, meal_context
                FROM glucose_readings
                WHERE patient_id = ?
                ORDER BY reading_time DESC, reading_id DESC
                LIMIT 3
                """;

        List<GlucoseReadingRecord> readings = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, patientId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    readings.add(mapReading(rs));
                }
            }
        }

        return readings;
    }

    // 2. Insert a new glucose reading
    // The database assigns the current timestamp automatically.
    // Returns the generated reading ID.
    public long addGlucoseReading(
            long patientId,
            BigDecimal glucoseValue,
            String status,
            String mealContext) throws SQLException {

        if (patientId <= 0) {
            throw new IllegalArgumentException(
                    "Patient ID must be positive.");
        }

        if (glucoseValue == null
                || glucoseValue.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Glucose value must be greater than zero.");
        }

        if (status == null ||
                !List.of("Normal", "Low", "High").contains(status)) {
            throw new IllegalArgumentException(
                    "Status must be Normal, Low, or High.");
        }

        if (mealContext == null ||
                !List.of("Pre-meal", "Post-meal").contains(mealContext)) {
            throw new IllegalArgumentException(
                    "Meal context must be Pre-meal or Post-meal.");
        }

        String sql = """
                INSERT INTO glucose_readings
                    (patient_id, glucose_value, reading_time,
                     status, meal_context)
                VALUES (?, ?, CURRENT_TIMESTAMP, ?, ?)
                RETURNING reading_id
                """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, patientId);
            ps.setBigDecimal(2, glucoseValue);
            ps.setString(3, status);
            ps.setString(4, mealContext);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("reading_id");
                }
            }
        }

        throw new SQLException("Failed to insert glucose reading.");
    }

    // 3. Retrieve all glucose readings for a patient
    public List<GlucoseReadingRecord> getReadingsByPatientId(
            long patientId) throws SQLException {

        String sql = """
                SELECT reading_id, patient_id, glucose_value,
                       reading_time, status, meal_context
                FROM glucose_readings
                WHERE patient_id = ?
                ORDER BY reading_time DESC, reading_id DESC
                """;

        List<GlucoseReadingRecord> readings = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, patientId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    readings.add(mapReading(rs));
                }
            }
        }

        return readings;
    }

    // Converts a database row into a Java object
    private GlucoseReadingRecord mapReading(ResultSet rs)
            throws SQLException {

        OffsetDateTime readingTime =
                rs.getObject("reading_time", OffsetDateTime.class);

        return new GlucoseReadingRecord(
                rs.getLong("reading_id"),
                rs.getLong("patient_id"),
                rs.getBigDecimal("glucose_value"),
                readingTime,
                rs.getString("status"),
                rs.getString("meal_context")
        );
    }

    // Test the DAO independently
    public static void main(String[] args) {

        GlucoseDAO dao = new GlucoseDAO();

        // Change this ID to the patient you want to test.
        long patientId = 1;

        try {
            System.out.println("LATEST THREE READINGS");

            List<GlucoseReadingRecord> latest =
                    dao.getLastThreeReadings(patientId);

            for (GlucoseReadingRecord reading : latest) {
                System.out.println(reading);
            }

            System.out.println("\nALL READINGS FOR PATIENT");

            List<GlucoseReadingRecord> all =
                    dao.getReadingsByPatientId(patientId);

            for (GlucoseReadingRecord reading : all) {
                System.out.println(reading);
            }


            //temporary should be deleted later for application
            /*long newReadingId = dao.addGlucoseReading(
             1,
            new BigDecimal("130.00"),
             "Normal",
             "Post-meal"
            );

            System.out.println("Inserted reading ID: " + newReadingId);*/
            //till this

        } catch (SQLException e) {
            System.err.println(
                    "Error accessing glucose readings: "
                    + e.getMessage());
            e.printStackTrace();
        }
    }
}

