package com.glucosync.repository;

import com.glucosync.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    // Spring Data JPA automatically handles basic CRUD and we can add custom finders here later
}
