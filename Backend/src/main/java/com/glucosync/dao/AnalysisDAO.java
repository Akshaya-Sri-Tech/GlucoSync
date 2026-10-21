
package com.glucosync.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public class AnalysisDAO {

    public static class GlucoseRecord {

        private final long readingId;
        private final long patientId;
        private final BigDecimal glucoseValue;
        private final OffsetDateTime readingTime;
        private final String status;
        private final String mealContext;

        public GlucoseRecord(
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
    }

    // Retrieve all readings from the last three days for one patient
    public List<GlucoseRecord> getLastThreeDays(long patientId)
            throws SQLException {

        if (patientId <= 0) {
            throw new IllegalArgumentException(
                    "Patient ID must be greater than zero.");
        }

        String sql = """
                SELECT reading_id,
                       patient_id,
                       glucose_value,
                       reading_time,
                       status,
                       meal_context
                FROM public.glucose_readings
                WHERE patient_id = ?
                  AND reading_time >=
                      CURRENT_TIMESTAMP - INTERVAL '3 days'
                ORDER BY reading_time DESC, reading_id DESC
                """;

        List<GlucoseRecord> readings = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, patientId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    OffsetDateTime readingTime =
                            rs.getObject(
                                    "reading_time",
                                    OffsetDateTime.class
                            );

                    GlucoseRecord record = new GlucoseRecord(
                            rs.getLong("reading_id"),
                            rs.getLong("patient_id"),
                            rs.getBigDecimal("glucose_value"),
                            readingTime,
                            rs.getString("status"),
                            rs.getString("meal_context")
                    );

                    readings.add(record);
                }
            }
        }

        return readings;
    }
}
