
package com.glucosync.controller;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.glucosync.dao.AnalysisDAO;
import com.glucosync.dao.AnalysisDAO.GlucoseRecord;

@RestController
@RequestMapping("/api/analysis")
@CrossOrigin(origins = "*")
public class AnalysisController {

    private final AnalysisDAO analysisDAO;

    public AnalysisController(AnalysisDAO analysisDAO) {
        this.analysisDAO = analysisDAO;
    }

    @PostMapping("/classify")
    public ResponseEntity<?> classifyRisk(
        @RequestBody Map<String, Long> request) {

    Long patientId = request.get("patientId");

    if (patientId == null || patientId <= 0) {
        return ResponseEntity.badRequest()
                .body(Map.of("error", "Invalid patient ID"));
    }

    try {
        List<AnalysisDAO.GlucoseRecord> readings =
                analysisDAO.getLastThreeDays(patientId);

        if (readings.isEmpty()) {
            return ResponseEntity.ok(
                    Map.of("risk", "INSUFFICIENT_DATA",
                           "readingCount", 0)
            );
        }

        long highCount = readings.stream()
                .filter(r -> "High".equalsIgnoreCase(r.getStatus()))
                .count();

        long lowCount = readings.stream()
                .filter(r -> "Low".equalsIgnoreCase(r.getStatus()))
                .count();

        String risk;

        if (highCount >= 3) {
            risk = "HIGH";
        } else if (lowCount > 0) {
            risk = "LOW";
        } else {
            risk = "MODERATE";
        }

        return ResponseEntity.ok(Map.of(
                "risk", risk,
                "readingCount", readings.size(),
                "highCount", highCount,
                "lowCount", lowCount
        ));

    } catch (SQLException e) {
        e.printStackTrace();

        return ResponseEntity.status(500)
                .body(Map.of("error", "Analysis failed"));
    }
}

    // Retrieve glucose history for the last three days
    @GetMapping("/patients/{patientId}/glucose")
    public ResponseEntity<?> getGlucoseAnalysis(
            @PathVariable long patientId) {

        try {
            List<GlucoseRecord> readings =
                    analysisDAO.getLastThreeDays(patientId);

            return ResponseEntity.ok(
                    Map.of(
                            "patientId", patientId,
                            "periodDays", 3,
                            "readingCount", readings.size(),
                            "readings", readings
                    )
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));

        } catch (SQLException e) {

            e.printStackTrace();

            return ResponseEntity.status(
                    HttpStatus.INTERNAL_SERVER_ERROR
            ).body(Map.of(
                    "error", "Unable to retrieve glucose history"
            ));
        }
    }
}
