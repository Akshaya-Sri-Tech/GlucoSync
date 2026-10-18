
USE GlucoSync;

-- 1. Insert sample patients
INSERT INTO Patients
    (name, age, diabetes_type, min_glucose, max_glucose)
VALUES
    ('Arun Kumar', 45, 'Type 2', 70, 140),
    ('Priya Sharma', 32, 'Type 1', 80, 180);

-- 2. Insert sample doctors
INSERT INTO Doctors (name)
VALUES
    ('Dr. Meera'),
    ('Dr. Rahul');

-- 3. Insert glucose readings
INSERT INTO Glucose_Readings
    (patient_id, glucose_value, reading_time, status, meal_context)
VALUES
    (1, 165, NOW(), 'High', 'Pre-meal'),
    (1, 110, NOW(), 'Normal', 'Post-meal'),
    (2, 105, NOW(), 'Normal', 'Pre-meal');

-- 4. Insert appointment slots for tomorrow
INSERT INTO Doctor_Slots
    (doctor_id, slot_date, start_time, end_time, available)
VALUES
    (1, DATE_ADD(CURDATE(), INTERVAL 1 DAY),
        '10:00:00', '10:30:00', TRUE),
    (2, DATE_ADD(CURDATE(), INTERVAL 1 DAY),
        '11:00:00', '11:30:00', TRUE);

-- 5. Book Arun's appointment with Dr. Meera
INSERT INTO Appointments
    (patient_id, doctor_id, slot_id, status)
SELECT
    1, 1, slot_id, 'Booked'
FROM Doctor_Slots
WHERE doctor_id = 1
  AND slot_date = DATE_ADD(CURDATE(), INTERVAL 1 DAY)
  AND start_time = '10:00:00'
  AND available = TRUE;

-- Mark the booked slot as unavailable
UPDATE Doctor_Slots
SET available = FALSE
WHERE doctor_id = 1
  AND slot_date = DATE_ADD(CURDATE(), INTERVAL 1 DAY)
  AND start_time = '10:00:00';

-- 6. Verify inserted data
SELECT * FROM Patients;
SELECT * FROM Doctors;
SELECT * FROM Glucose_Readings;
SELECT * FROM Doctor_Slots;
SELECT * FROM Appointments;
