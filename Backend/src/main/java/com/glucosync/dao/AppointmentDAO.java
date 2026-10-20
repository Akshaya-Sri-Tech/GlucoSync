package com.glucosync.dao;

import com.glucosync.dao.DatabaseConnection;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {

    // Represents an available doctor slot
    public static class DoctorSlotRecord {

        private long slotId;
        private long doctorId;
        private String doctorName;
        private Date slotDate;
        private Time startTime;
        private Time endTime;

        public DoctorSlotRecord(
                long slotId,
                long doctorId,
                String doctorName,
                Date slotDate,
                Time startTime,
                Time endTime) {

            this.slotId = slotId;
            this.doctorId = doctorId;
            this.doctorName = doctorName;
            this.slotDate = slotDate;
            this.startTime = startTime;
            this.endTime = endTime;
        }

        public long getSlotId() {
            return slotId;
        }

        public long getDoctorId() {
            return doctorId;
        }

        public String getDoctorName() {
            return doctorName;
        }

        public Date getSlotDate() {
            return slotDate;
        }

        public Time getStartTime() {
            return startTime;
        }

        public Time getEndTime() {
            return endTime;
        }

        @Override
        public String toString() {
            return "Slot ID: " + slotId
                    + ", Doctor: " + doctorName
                    + ", Date: " + slotDate
                    + ", Time: " + startTime
                    + " - " + endTime;
        }
    }

    // Represents an appointment with patient and doctor details
    public static class AppointmentRecord {

        private long appointmentId;
        private long patientId;
        private String patientName;
        private long doctorId;
        private String doctorName;
        private long slotId;
        private Date appointmentDate;
        private Time startTime;
        private Time endTime;
        private String status;

        public AppointmentRecord(
                long appointmentId,
                long patientId,
                String patientName,
                long doctorId,
                String doctorName,
                long slotId,
                Date appointmentDate,
                Time startTime,
                Time endTime,
                String status) {

            this.appointmentId = appointmentId;
            this.patientId = patientId;
            this.patientName = patientName;
            this.doctorId = doctorId;
            this.doctorName = doctorName;
            this.slotId = slotId;
            this.appointmentDate = appointmentDate;
            this.startTime = startTime;
            this.endTime = endTime;
            this.status = status;
        }

        public long getAppointmentId() {
            return appointmentId;
        }

        public long getPatientId() {
            return patientId;
        }

        public String getPatientName() {
            return patientName;
        }

        public long getDoctorId() {
            return doctorId;
        }

        public String getDoctorName() {
            return doctorName;
        }

        public long getSlotId() {
            return slotId;
        }

        public Date getAppointmentDate() {
            return appointmentDate;
        }

        public Time getStartTime() {
            return startTime;
        }

        public Time getEndTime() {
            return endTime;
        }

        public String getStatus() {
            return status;
        }

        @Override
        public String toString() {
            return "Appointment ID: " + appointmentId
                    + ", Patient: " + patientName
                    + " (ID: " + patientId + ")"
                    + ", Doctor: " + doctorName
                    + " (ID: " + doctorId + ")"
                    + ", Slot ID: " + slotId
                    + ", Date: " + appointmentDate
                    + ", Time: " + startTime + " - " + endTime
                    + ", Status: " + status;
        }
    }

    // 1. Retrieve all available doctor slots
    public List<DoctorSlotRecord> getAvailableSlots()
            throws SQLException {

        String sql = """
                SELECT s.slot_id, s.doctor_id, d.name AS doctor_name,
                       s.slot_date, s.start_time, s.end_time
                FROM doctor_slots s
                JOIN doctors d ON s.doctor_id = d.doctor_id
                WHERE s.available = TRUE
                  AND s.slot_date >= CURRENT_DATE
                ORDER BY s.slot_date, s.start_time
                """;

        List<DoctorSlotRecord> slots = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                slots.add(new DoctorSlotRecord(
                        rs.getLong("slot_id"),
                        rs.getLong("doctor_id"),
                        rs.getString("doctor_name"),
                        rs.getDate("slot_date"),
                        rs.getTime("start_time"),
                        rs.getTime("end_time")
                ));
            }
        }

        return slots;
    }

    // 2. Book an appointment using a patient ID and slot ID
    // Returns the generated appointment ID.
    public long bookAppointment(long patientId, long slotId)
            throws SQLException {

        if (patientId <= 0 || slotId <= 0) {
            throw new IllegalArgumentException(
                    "Patient ID and slot ID must be positive.");
        }

        String lockSlotSql = """
                SELECT doctor_id
                FROM doctor_slots
                WHERE slot_id = ?
                  AND available = TRUE
                  AND slot_date >= CURRENT_DATE
                FOR UPDATE
                """;

        String insertAppointmentSql = """
                INSERT INTO appointments
                    (patient_id, doctor_id, slot_id, status)
                VALUES (?, ?, ?, ?)
                RETURNING appointment_id
                """;

        String updateSlotSql = """
                UPDATE doctor_slots
                SET available = FALSE
                WHERE slot_id = ?
                """;

        try (Connection con = DatabaseConnection.getConnection()) {

            // Begin transaction
            con.setAutoCommit(false);

            try {
                long doctorId;

                // Lock the slot to prevent concurrent double-booking
                try (PreparedStatement ps =
                             con.prepareStatement(lockSlotSql)) {

                    ps.setLong(1, slotId);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException(
                                    "Slot is unavailable, does not exist, "
                                    + "or its date has passed.");
                        }

                        doctorId = rs.getLong("doctor_id");
                    }
                }

                long appointmentId;

                // Create appointment
                try (PreparedStatement ps =
                             con.prepareStatement(insertAppointmentSql)) {

                    ps.setLong(1, patientId);
                    ps.setLong(2, doctorId);
                    ps.setLong(3, slotId);
                    ps.setString(4, "Booked");

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException(
                                    "Failed to create appointment.");
                        }

                        appointmentId =
                                rs.getLong("appointment_id");
                    }
                }

                // Reserve the slot
                try (PreparedStatement ps =
                             con.prepareStatement(updateSlotSql)) {

                    ps.setLong(1, slotId);

                    if (ps.executeUpdate() != 1) {
                        throw new SQLException(
                                "Failed to reserve the doctor slot.");
                    }
                }

                con.commit();
                return appointmentId;

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
                } catch (SQLException resetError) {
                    // Closing the connection below releases its resources.
                }
            }
        }
    }

    // 3. Retrieve all appointments belonging to a patient
    public List<AppointmentRecord> getAppointmentsByPatientId(
            long patientId) throws SQLException {

        String sql = """
                SELECT a.appointment_id,
                       a.patient_id,
                       p.name AS patient_name,
                       a.doctor_id,
                       d.name AS doctor_name,
                       a.slot_id,
                       s.slot_date,
                       s.start_time,
                       s.end_time,
                       a.status
                FROM appointments a
                JOIN patients p ON a.patient_id = p.patient_id
                JOIN doctors d ON a.doctor_id = d.doctor_id
                JOIN doctor_slots s ON a.slot_id = s.slot_id
                WHERE a.patient_id = ?
                ORDER BY s.slot_date DESC, s.start_time DESC
                """;

        List<AppointmentRecord> appointments = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, patientId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    appointments.add(mapAppointment(rs));
                }
            }
        }

        return appointments;
    }

    // 4. Retrieve all appointments belonging to a doctor
    public List<AppointmentRecord> getAppointmentsByDoctorId(
            long doctorId) throws SQLException {

        String sql = """
                SELECT a.appointment_id,
                       a.patient_id,
                       p.name AS patient_name,
                       a.doctor_id,
                       d.name AS doctor_name,
                       a.slot_id,
                       s.slot_date,
                       s.start_time,
                       s.end_time,
                       a.status
                FROM appointments a
                JOIN patients p ON a.patient_id = p.patient_id
                JOIN doctors d ON a.doctor_id = d.doctor_id
                JOIN doctor_slots s ON a.slot_id = s.slot_id
                WHERE a.doctor_id = ?
                ORDER BY s.slot_date, s.start_time
                """;

        List<AppointmentRecord> appointments = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, doctorId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    appointments.add(mapAppointment(rs));
                }
            }
        }

        return appointments;
    }

    // Helper: map a result row to an appointment record
    private AppointmentRecord mapAppointment(ResultSet rs)
            throws SQLException {

        return new AppointmentRecord(
                rs.getLong("appointment_id"),
                rs.getLong("patient_id"),
                rs.getString("patient_name"),
                rs.getLong("doctor_id"),
                rs.getString("doctor_name"),
                rs.getLong("slot_id"),
                rs.getDate("slot_date"),
                rs.getTime("start_time"),
                rs.getTime("end_time"),
                rs.getString("status")
        );
    }

    // Test the DAO
    public static void main(String[] args) {

        AppointmentDAO dao = new AppointmentDAO();

        try {
            System.out.println("AVAILABLE DOCTOR SLOTS");

            for (DoctorSlotRecord slot : dao.getAvailableSlots()) {
                System.out.println(slot);
            }

            System.out.println("\nPATIENT APPOINTMENTS");

            for (AppointmentRecord appointment :
                    dao.getAppointmentsByPatientId(1)) {
                System.out.println(appointment);
            }

            System.out.println("\nDOCTOR APPOINTMENTS");

            for (AppointmentRecord appointment :
                    dao.getAppointmentsByDoctorId(1)) {
                System.out.println(appointment);
            }

        } catch (SQLException e) {
            System.err.println(
                    "Appointment DAO error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
