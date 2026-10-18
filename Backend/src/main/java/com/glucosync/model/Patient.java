package com.glucosync.model;

import jakarta.persistence.*;

@Entity
@Table(name = "patients")
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long patient_id;

    private String name;
    private Integer age;
    private String email;
    private String password;

    public Patient() {}

    // Getters and Setters
    public Long getPatient_id() { return patient_id; }
    public void setPatient_id(Long patient_id) { this.patient_id = patient_id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}