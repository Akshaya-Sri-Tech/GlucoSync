package com.glucosync.controller;

import com.glucosync.model.Patient;
import com.glucosync.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins = "*")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @GetMapping
    public List<Patient> getAllPatients() {
        return patientService.getAllPatients();
    }

    @GetMapping("/{id}")
    public Patient getPatientById(@PathVariable Long id) {
        return patientService.getPatientById(id).orElse(null);
    }

    @PostMapping("/register")
    public Patient registerPatient(@RequestBody Patient patient) {
        return patientService.savePatient(patient);
    }
}