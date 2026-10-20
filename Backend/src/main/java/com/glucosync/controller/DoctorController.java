package com.glucosync.controller;

import com.glucosync.dao.DoctorDAO;
import com.glucosync.dao.DoctorDAO.DoctorSlotRecord;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doctors")
@CrossOrigin(origins = "*")
public class DoctorController {

    private final DoctorDAO doctorDAO;

    public DoctorController(DoctorDAO doctorDAO) {
        this.doctorDAO = doctorDAO;
    }

    @GetMapping("/{doctorId}/slots")
    public ResponseEntity<?> getDoctorSlots(
            @PathVariable long doctorId,
            @RequestParam LocalDate date) {
        if (doctorId <= 0) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Doctor ID must be positive"));
        }
        try {
            if (doctorDAO.getDoctorName(doctorId).isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(doctorDAO.getSlots(doctorId, date));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Unable to retrieve doctor slots"));
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Unable to retrieve doctor slots"));
        }
    }

    @PostMapping("/{doctorId}/slots")
    public ResponseEntity<?> addDoctorSlots(
            @PathVariable long doctorId,
            @RequestBody(required = false) SlotRequest request) {
        if (doctorId <= 0) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Doctor ID must be positive"));
        }
        if (request == null || request.date() == null
                || request.startTimes() == null || request.startTimes().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "date and at least one start time are required"));
        }
        if (request.date().isBefore(LocalDate.now())
                || request.startTimes().stream().anyMatch(time -> time == null
                    || !time.equals(time.withSecond(0).withNano(0))
                    || !time.plusMinutes(30).isAfter(time))) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Date must be today or later and times must allow a 30 minute slot"));
        }
        if (request.startTimes().stream().distinct().count()
                != request.startTimes().size()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Start times must be unique"));
        }
        try {
            if (doctorDAO.getDoctorName(doctorId).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Doctor does not exist"));
            }
            List<DoctorSlotRecord> slots = doctorDAO.addSlots(
                    doctorId, request.date(), request.startTimes());
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "Appointment slots saved successfully",
                    "slots", slots));
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("error", "One or more slots already exist"));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Unable to save appointment slots"));
        } catch (DataAccessException e) {
            SQLException sqlException = findSQLException(e);
            if (sqlException != null && "23505".equals(sqlException.getSQLState())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("error", "One or more slots already exist"));
            }
            if (sqlException != null && "23503".equals(sqlException.getSQLState())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Doctor does not exist"));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Unable to save appointment slots"));
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

    public record SlotRequest(LocalDate date, List<LocalTime> startTimes) {
    }
}
