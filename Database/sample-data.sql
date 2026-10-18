
-- ============================================
-- GlucoSync - Sample Data
-- Database: PostgreSQL / Supabase
-- ============================================

-- 1. PATIENTS
-- Insert only if the patient does not already exist.

INSERT INTO patients
    (name, age, diabetes_type, min_glucose, max_glucose)
SELECT v.name, v.age, v.diabetes_type, v.min_glucose, v.max_glucose
FROM (VALUES
    ('Arun Kumar', 45, 'Type 2', 80.00::NUMERIC, 180.00::NUMERIC),
    ('Priya Sharma', 32, 'Type 1', 80.00::NUMERIC, 180.00::NUMERIC),
    ('Karthik Raj', 55, 'Type 2', 70.00::NUMERIC, 160.00::NUMERIC)
) AS v(name, age, diabetes_type, min_glucose, max_glucose)
WHERE NOT EXISTS (
    SELECT 1 FROM patients p WHERE p.name = v.name
);


-- 2. DOCTORS

INSERT INTO doctors (name)
SELECT v.name
FROM (VALUES
    ('Dr. Meera Krishnan'),
    ('Dr. Rahul Menon'),
    ('Dr. Ananya Rao')
) AS v(name)
WHERE NOT EXISTS (
    SELECT 1 FROM doctors d WHERE d.name = v.name
);


-- 3. GLUCOSE READINGS

INSERT INTO glucose_readings
    (patient_id, glucose_value, reading_time, status, meal_context)
SELECT p.patient_id,
       v.glucose_value,
       CURRENT_TIMESTAMP - v.minutes_ago * INTERVAL '1 minute',
       v.status,
       v.meal_context
FROM (VALUES
    ('Arun Kumar', 115.00::NUMERIC, 120, 'Normal', 'Pre-meal'),
    ('Arun Kumar', 185.00::NUMERIC, 60, 'High', 'Post-meal'),
    ('Priya Sharma', 75.00::NUMERIC, 180, 'Low', 'Pre-meal'),
    ('Priya Sharma', 140.00::NUMERIC, 30, 'Normal', 'Post-meal'),
    ('Karthik Raj', 130.00::NUMERIC, 90, 'Normal', 'Pre-meal'),
    ('Karthik Raj', 175.00::NUMERIC, 15, 'High', 'Post-meal')
) AS v(patient_name, glucose_value, minutes_ago, status, meal_context)
JOIN patients p ON p.name = v.patient_name
WHERE NOT EXISTS (
    SELECT 1
    FROM glucose_readings g
    WHERE g.patient_id = p.patient_id
      AND g.glucose_value = v.glucose_value
      AND g.meal_context = v.meal_context
      AND g.reading_time >= CURRENT_TIMESTAMP - INTERVAL '4 hours'
);


-- 4. DOCTOR APPOINTMENT SLOTS

INSERT INTO doctor_slots
    (doctor_id, slot_date, start_time, end_time, available)
SELECT d.doctor_id,
       v.slot_date::DATE,
       v.start_time::TIME,
       v.end_time::TIME,
       TRUE
FROM (VALUES
    ('Dr. Meera Krishnan', CURRENT_DATE + 1, '09:00', '09:30'),
    ('Dr. Meera Krishnan', CURRENT_DATE + 1, '10:00', '10:30'),
    ('Dr. Rahul Menon', CURRENT_DATE + 1, '11:00', '11:30'),
    ('Dr. Rahul Menon', CURRENT_DATE + 2, '14:00', '14:30'),
    ('Dr. Ananya Rao', CURRENT_DATE + 2, '15:00', '15:30')
) AS v(doctor_name, slot_date, start_time, end_time)
JOIN doctors d ON d.name = v.doctor_name
WHERE NOT EXISTS (
    SELECT 1
    FROM doctor_slots s
    WHERE s.doctor_id = d.doctor_id
      AND s.slot_date = v.slot_date::DATE
      AND s.start_time = v.start_time::TIME
);


-- 5. SAMPLE APPOINTMENTS

WITH requested(patient_name, doctor_name, slot_date, start_time) AS (
    VALUES
        ('Arun Kumar', 'Dr. Meera Krishnan',
         CURRENT_DATE + 1, '09:00'::TIME),
        ('Priya Sharma', 'Dr. Rahul Menon',
         CURRENT_DATE + 1, '11:00'::TIME)
)
INSERT INTO appointments (patient_id, doctor_id, slot_id, status)
SELECT p.patient_id, d.doctor_id, s.slot_id, 'Booked'
FROM requested r
JOIN patients p ON p.name = r.patient_name
JOIN doctors d ON d.name = r.doctor_name
JOIN doctor_slots s
  ON s.doctor_id = d.doctor_id
 AND s.slot_date = r.slot_date
 AND s.start_time = r.start_time
WHERE s.available = TRUE
  AND NOT EXISTS (
      SELECT 1
      FROM appointments a
      WHERE a.slot_id = s.slot_id
  );


-- 6. UPDATE BOOKED SLOTS

UPDATE doctor_slots s
SET available = FALSE
WHERE EXISTS (
    SELECT 1
    FROM appointments a
    WHERE a.slot_id = s.slot_id
      AND a.status = 'Booked'
);


-- 7. VERIFY DATA

SELECT * FROM patients ORDER BY patient_id;
SELECT * FROM doctors ORDER BY doctor_id;
SELECT * FROM glucose_readings ORDER BY reading_id;
SELECT * FROM doctor_slots ORDER BY slot_date, start_time;
SELECT * FROM appointments ORDER BY appointment_id;
