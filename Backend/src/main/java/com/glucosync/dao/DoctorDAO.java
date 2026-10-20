package com.glucosync.dao;

import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class DoctorDAO {

    public record DoctorSlotRecord(long slotId, long doctorId,
                                   LocalDate slotDate, LocalTime startTime,
                                   LocalTime endTime, boolean available) {
    }

    public Optional<String> getDoctorName(long doctorId) throws SQLException {
        String sql = "SELECT name FROM doctors WHERE doctor_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, doctorId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(rs.getString("name"))
                        : Optional.empty();
            }
        }
    }

    public List<DoctorSlotRecord> getSlots(long doctorId, LocalDate date)
            throws SQLException {
        String sql = """
                SELECT slot_id, doctor_id, slot_date, start_time, end_time,
                       available
                FROM doctor_slots
                WHERE doctor_id = ? AND slot_date = ?
                ORDER BY start_time
                """;
        List<DoctorSlotRecord> slots = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, doctorId);
            ps.setDate(2, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    slots.add(new DoctorSlotRecord(
                            rs.getLong("slot_id"), rs.getLong("doctor_id"),
                            rs.getDate("slot_date").toLocalDate(),
                            rs.getTime("start_time").toLocalTime(),
                            rs.getTime("end_time").toLocalTime(),
                            rs.getBoolean("available")));
                }
            }
        }
        return slots;
    }

    public List<DoctorSlotRecord> addSlots(long doctorId, LocalDate date,
                                            List<LocalTime> startTimes)
            throws SQLException {
        String sql = """
                INSERT INTO doctor_slots
                    (doctor_id, slot_date, start_time, end_time, available)
                VALUES (?, ?, ?, ?, TRUE)
                RETURNING slot_id, doctor_id, slot_date, start_time,
                          end_time, available
                """;
        List<DoctorSlotRecord> added = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                for (LocalTime startTime : startTimes) {
                    ps.setLong(1, doctorId);
                    ps.setDate(2, Date.valueOf(date));
                    ps.setTime(3, Time.valueOf(startTime));
                    ps.setTime(4, Time.valueOf(startTime.plusMinutes(30)));
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("Slot creation failed.");
                        }
                        added.add(new DoctorSlotRecord(
                                rs.getLong("slot_id"),
                                rs.getLong("doctor_id"),
                                rs.getDate("slot_date").toLocalDate(),
                                rs.getTime("start_time").toLocalTime(),
                                rs.getTime("end_time").toLocalTime(),
                                rs.getBoolean("available")));
                    }
                }
                con.commit();
                return added;
            } catch (SQLException | RuntimeException e) {
                try {
                    con.rollback();
                } catch (SQLException rollbackError) {
                    e.addSuppressed(rollbackError);
                }
                throw e;
            } finally {
                try {
                    con.setAutoCommit(true);
                } catch (SQLException ignored) {
                    // Closing the connection releases transaction resources.
                }
            }
        }
    }
}
