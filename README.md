# GlucoSync 🩺
### A Java-Based Glucose Monitoring and Doctor Appointment Management System

## 📌 Project Overview

GlucoSync is a Java-based glucose monitoring system that connects patients and doctors through personalized glucose tracking, threshold-based alert notifications, and appointment management.

Doctors register patients, assign unique patient IDs, configure individual pre-meal and post-meal glucose thresholds, and provide available appointment slots. Patients log in using their patient ID and password to record their glucose readings.

When a patient's last three pre-meal or post-meal readings exceed the corresponding configured threshold, the system triggers an alert and prompts the patient to book an available doctor appointment.

GlucoSync aims to support timely medical follow-up through automated glucose monitoring and appointment scheduling.

## 🎯 Key Features

### 👨‍⚕️ Doctor Module
- **Patient Registration:** Register patients and generate unique patient IDs.
- **Personalized Threshold Configuration:** Set minimum and maximum glucose thresholds separately for pre-meal and post-meal readings for each patient.
- **Availability Management:** Enter available appointment slots with dates and timings.
- **Patient Monitoring:** Configure patient-specific monitoring limits to support glucose tracking.

### 🧑‍💻 Patient Module
- **Secure Login:** Log in using a doctor-assigned patient ID and password.
- **Glucose Recording:** Enter pre-meal and post-meal glucose readings.
- **Glucose History:** Maintain records of submitted glucose readings.
- **Automated Alert Notifications:** Trigger an alert when the last three pre-meal or post-meal readings exceed the corresponding configured maximum threshold.
- **Urgent Appointment Booking:** Prompt the patient to book an available doctor appointment after an alert.
- **Appointment Confirmation:** Book an available doctor slot and display a confirmation message.

professional medical advice, diagnosis, or treatment. Glucose alerts are intended to support monitoring and follow-up and should not be used as the sole basis for medical decisions.
