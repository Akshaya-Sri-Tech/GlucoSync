package com.glucosync.controller;

import com.glucosync.dao.GlucoseDAO;
import com.glucosync.dao.GlucoseDAO.GlucoseReadingRecord;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/glucose")
@CrossOrigin(origins = "*")
public class GlucoseController {

    private final GlucoseDAO glucoseDAO;

    public GlucoseController(GlucoseDAO glucoseDAO) {
        this.glucoseDAO = glucoseDAO;
    }

    // Insert a new glucose reading
    @PostMapping("/readings")
    public ResponseEntity<?> addGlucoseReading(
            @RequestBody GlucoseRequest request) {

        try {
            long readingId = glucoseDAO.addGlucoseReading(
                    request.patientId(),
                    request.glucoseValue(),
                    request.status(),
                    request.mealContext()
            );

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", "Glucose reading saved successfully",
                            "readingId", readingId
                    ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));

        } catch (SQLException e) {
            e.printStackTrace();

            return ResponseEntity.status(
                    HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "Unable to save glucose reading",
                            "details", e.getMessage() == null
                            ? "Unknown database error"
                            : e.getMessage()
                    ));
        }
    }

    // Retrieve all readings for a patient
    @GetMapping("/readings/{patientId}")
    public ResponseEntity<?> getReadings(
            @PathVariable long patientId) {

        if (patientId <= 0) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Patient ID must be positive"));
        }

        try {
            List<GlucoseReadingRecord> readings
                    = glucoseDAO.getReadingsByPatientId(patientId);

            return ResponseEntity.ok(readings);

        } catch (SQLException e) {
            return ResponseEntity.status(
                    HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "Unable to retrieve glucose readings"
                    ));
        }
    }

    // JSON structure for a glucose reading
    public record GlucoseRequest(
            long patientId,
            BigDecimal glucoseValue,
            String status,
            String mealContext
    ) {
    }
}
