package com.glucosync.controller;

import com.glucosync.dao.AppointmentDAO;
import com.glucosync.dao.AppointmentDAO.DoctorSlotRecord;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "*")
public class AppointmentController {

    private final AppointmentDAO appointmentDAO;

    public AppointmentController(AppointmentDAO appointmentDAO) {
        this.appointmentDAO = appointmentDAO;
    }

    @GetMapping("/slots")
    public ResponseEntity<?> getAvailableSlots() {
        try {
            List<DoctorSlotRecord> slots = appointmentDAO.getAvailableSlots();
            return ResponseEntity.ok(slots);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Unable to retrieve available slots"));
        }
    }

    @PostMapping
    public ResponseEntity<?> bookAppointment(@RequestBody(required = false)
                                              AppointmentRequest request) {
        if (request == null || request.patientId() == null
                || request.slotId() == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "patientId and slotId are required"));
        }

        if (request.patientId() <= 0 || request.slotId() <= 0) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "patientId and slotId must be positive"));
        }

        try {
            long appointmentId = appointmentDAO.bookAppointment(
                    request.patientId(), request.slotId());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", "Appointment booked successfully",
                            "appointmentId", appointmentId,
                            "patientId", request.patientId(),
                            "slotId", request.slotId(),
                            "status", "Booked"
                    ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (SQLException e) {
            if ("23503".equals(e.getSQLState())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Patient does not exist"));
            }
            if (isUnavailableSlot(e)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("error", "Slot is unavailable or has passed"));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Unable to book appointment"));
        } catch (DataAccessException e) {
            SQLException sqlException = findSQLException(e);
            if (sqlException != null
                    && "23503".equals(sqlException.getSQLState())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Patient does not exist"));
            }
            if (sqlException != null && isUnavailableSlot(sqlException)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("error", "Slot is unavailable or has passed"));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Unable to book appointment"));
        }
    }

    private SQLException findSQLException(Throwable exception) {
        for (Throwable current = exception; current != null;
             current = current.getCause()) {
            if (current instanceof SQLException sqlException) {
                return sqlException;
            }
        }
        return null;
    }

    private boolean isUnavailableSlot(SQLException exception) {
        for (SQLException current = exception; current != null;
             current = current.getNextException()) {
            if (current.getMessage() != null
                    && current.getMessage().startsWith("Slot is unavailable")) {
                return true;
            }
            if ("23505".equals(current.getSQLState())) {
                return true;
            }
        }
        return false;
    }

    public record AppointmentRequest(Long patientId, Long slotId) {
    }
}
