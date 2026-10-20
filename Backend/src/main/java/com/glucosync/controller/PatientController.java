
package com.glucosync.controller;

import com.glucosync.dao.PatientDAO;
import com.glucosync.dao.PatientDAO.PatientRecord;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins = "*")
public class PatientController {

    private final PatientDAO patientDAO;

    public PatientController(PatientDAO patientDAO) {
        this.patientDAO = patientDAO;
    }

    // Get all patients
    @GetMapping
    public ResponseEntity<?> getAllPatients() {
        try {
            List<PatientRecord> patients = patientDAO.getAllPatients();
            return ResponseEntity.ok(patients);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Unable to retrieve patients"));
        }
    }

    // Get one patient by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getPatientById(@PathVariable long id) {
        try {
            return patientDAO.getPatientById(id)
                    .<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Unable to retrieve patient"));
        }
    }

    // Register a patient
    @PostMapping("/register")
    public ResponseEntity<?> registerPatient(
            @RequestBody PatientRequest request) {
        try {
            long patientId = patientDAO.addPatient(
                    request.name(),
                    request.age(),
                    request.diabetesType(),
                    request.minGlucose(),
                    request.maxGlucose()
            );

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", "Patient registered successfully",
                            "patientId", patientId
                    ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Unable to register patient"));
        }
    }

    // Update a patient
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePatient(
            @PathVariable long id,
            @RequestBody PatientRequest request) {
        try {
            boolean updated = patientDAO.updatePatient(
                    id,
                    request.name(),
                    request.age(),
                    request.diabetesType(),
                    request.minGlucose(),
                    request.maxGlucose()
            );

            if (!updated) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(
                    Map.of("message", "Patient updated successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Unable to update patient"));
        }
    }

    // JSON request format for registration and update
    public record PatientRequest(
            String name,
            Integer age,
            String diabetesType,
            BigDecimal minGlucose,
            BigDecimal maxGlucose
    ) {}
}
