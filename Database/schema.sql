
-- GlucoSync Database Setup

CREATE DATABASE IF NOT EXISTS GlucoSync;
USE GlucoSync;

-- 1. Patients
CREATE TABLE IF NOT EXISTS Patients (
    patient_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    age INT,
    diabetes_type VARCHAR(20),
    min_glucose DECIMAL(6,2),
    max_glucose DECIMAL(6,2)
);

-- 2. Doctors
CREATE TABLE IF NOT EXISTS Doctors (
    doctor_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL
);

-- 3. Glucose Readings
CREATE TABLE IF NOT EXISTS Glucose_Readings (
    reading_id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT NOT NULL,
    glucose_value DECIMAL(6,2) NOT NULL,
    reading_time DATETIME NOT NULL,
    status ENUM('Normal', 'High', 'Low') NOT NULL,
    meal_context ENUM('Pre-meal', 'Post-meal') NOT NULL,

    FOREIGN KEY (patient_id)
        REFERENCES Patients(patient_id)
);

-- 4. Doctor Slots
CREATE TABLE IF NOT EXISTS Doctor_Slots (
    slot_id INT PRIMARY KEY AUTO_INCREMENT,
    doctor_id INT NOT NULL,
    slot_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    available BOOLEAN NOT NULL DEFAULT TRUE,

    FOREIGN KEY (doctor_id)
        REFERENCES Doctors(doctor_id),

    UNIQUE (doctor_id, slot_date, start_time),
    CHECK (end_time > start_time)
);

-- 5. Appointments
CREATE TABLE IF NOT EXISTS Appointments (
    appointment_id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    slot_id INT NOT NULL,
    status ENUM(
        'Pending',
        'Booked',
        'Cancelled',
        'Completed'
    ) NOT NULL DEFAULT 'Pending',

    FOREIGN KEY (patient_id)
        REFERENCES Patients(patient_id),

    FOREIGN KEY (doctor_id)
        REFERENCES Doctors(doctor_id),

    FOREIGN KEY (slot_id)
        REFERENCES Doctor_Slots(slot_id)
);

-- Verify table creation
SHOW TABLES;
