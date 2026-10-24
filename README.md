# GlucoSync 🩺
### Java-Based Glucose Monitoring and Doctor Appointment Management System

## 📌 Overview

GlucoSync is a Java-based system that monitors glucose readings, alerts patients when thresholds are exceeded, and enables doctor appointment booking.

## 🎯Key Features

### 👨‍⚕️Doctor
- Register patients and assign patient IDs and login credentials.
- Configure minimum and maximum glucose thresholds separately for pre-meal and post-meal readings.
- Enter available appointment dates and times.
- Manage doctor and patient information through the application.

### 🧑‍💻Patient
- Log in using a patient ID and password.
- Submit pre-meal and post-meal glucose readings.
- Store readings with their associated date and time.
- Receive an alert when the latest three pre-meal readings **or** the latest three post-meal readings exceed the corresponding configured maximum threshold.
- Book an available doctor appointment after an alert and receive a booking confirmation.

> **Alert rule:** The described rule checks whether all three most recent readings in either category are above that category's maximum threshold. Confirm this wording against the implemented backend logic before publishing if the code uses a different condition.

## 🛠️ Technology Stack

| Technology | Purpose |
|---|---|
| Java | Application and business logic |
| Swing | Desktop screens and appointment-booking interface |
| AWT | Alert notification interface |
| Spring Boot | Backend application and REST API |
| JDBC | Database connectivity |
| PostgreSQL / Supabase | Database, if this matches the current `DatabaseConnection.java` configuration |
| Maven | Dependency and build management |
| Git and GitHub | Version control and collaboration |

Check `Backend/pom.xml` and `Backend/src/main/resources/application.properties` for the exact dependency and database configuration used by the current version.

## 📂  Project Structure

The following structure reflects the source folders and files visible in the repository.

```text
GlucoSync/
├── Frontend/
│   ├── AWT/
│   │   └── AlertNotification.java
│   │
│   └── Swing/
│       ├── src/
│       ├── AlertNotification.java
│       ├── AppointmentBooking.java
│       ├── DoctorDashboard.java
│       ├── DoctorLogin.java
│       ├── GlucoSyncHome.java
│       ├── PatientDashboard.java
│       └── PatientLogin.java
│
├── Backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/
│   │   │   │       └── glucosync/
│   │   │   │           ├── controller/
│   │   │   │           │   ├── AnalysisController.java
│   │   │   │           │   ├── AppointmentController.java
│   │   │   │           │   ├── DoctorController.java
│   │   │   │           │   ├── GlucoseController.java
│   │   │   │           │   └── PatientController.java
│   │   │   │           │
│   │   │   │           ├── dao/
│   │   │   │           │   ├── AnalysisDAO.java
│   │   │   │           │   ├── AppointmentDAO.java
│   │   │   │           │   ├── DatabaseConnection.java
│   │   │   │           │   ├── DoctorDAO.java
│   │   │   │           │   ├── GlucoseDAO.java
│   │   │   │           │   └── PatientDAO.java
│   │   │   │           │
│   │   │   │           └── GlucoSyncApplication.java
│   │   │   │
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   │
│   │   └── test/
│   │       └── java/
│   │
│   ├── target/                 # Generated Maven build output
│   ├── .env                    # Local secrets; do not commit
│   └── pom.xml
│
├── Database/
│   ├── sample-data.sql
│   └── schema.sql
│
├── tools/
│   └── apache-maven-3.10.0/    # Include only if this is intentionally tracked
│
├── .gitignore
└── README.md
```

### 📂 Folder and file responsibilities

- **`Frontend/AWT/`** — AWT-based alert notification window.
- **`Frontend/Swing/`** — Swing desktop UI, including the home screen, patient and doctor login, dashboards, and appointment booking.
- **`Backend/.../controller/`** — REST API controllers for patient, doctor, glucose, appointment, and analysis operations.
- **`Backend/.../dao/`** — Database access classes and SQL operations.
- **`GlucoSyncApplication.java`** — Spring Boot application entry point.
- **`application.properties`** — Backend application configuration.
- **`Backend/pom.xml`** — Backend Maven dependencies and build configuration.
- **`Database/schema.sql`** — Database table definitions.
- **`Database/sample-data.sql`** — Sample records for development or testing.


## 🔄 System Workflow

1. The doctor registers a patient and assigns the patient's login details.
2. The doctor configures the patient's pre-meal and post-meal minimum and maximum glucose thresholds.
3. The doctor enters available appointment slots and timings.
4. The patient logs in with their patient ID and password.
5. The patient submits pre-meal and post-meal glucose readings.
6. The backend compares the latest three readings in each category against the corresponding configured maximum threshold.
7. If all three readings in either category exceed its maximum threshold, the application displays an alert.
8. The patient books an available doctor slot.
9. The application records the booking and displays a confirmation message.

## 🗄️Database

The `Database/` directory contains:

- `schema.sql` — creates the database schema.
- `sample-data.sql` — supplies sample data, if needed.

The database connection and credentials should be configured according to `Backend/src/main/resources/application.properties`, `Backend/.../DatabaseConnection.java`, and the environment-variable loading used by the application.

## ⚙️ Prerequisites

- A JDK compatible with the Java version configured in `Backend/pom.xml`.
- Apache Maven (the repository screenshot shows Maven 3.10.0 under `tools/`).
- Access to the configured database.
- Git, if cloning the repository.
- Any required environment variables for the database connection.

## 🚀 Setup and Installation

### 1. Clone the repository

```bash
git clone https://github.com/Akshaya-Sri-Tech/GlucoSync
cd GlucoSync
```

### 2. Configure the database

1. Create or access the database configured for the project.
2. Run `Database/schema.sql`.
3. Load `Database/sample-data.sql` if sample records are required.
4. Configure the database URL, username, and password using the mechanism expected by the backend.

### 3. Configure secrets safely

If the backend uses `Backend/.env`, create that file locally with the variable names expected by the code. Do not publish real database credentials or commit the `.env` file. A sanitized `.env.example` with placeholder values may be provided for other developers.

### 4. Build and run the backend

From the repository root:

```bash
cd Backend
mvn clean compile
```

Start the application using the Spring Boot Maven plugin if it is configured in `Backend/pom.xml`:

```bash
mvn spring-boot:run
```

If Maven is not installed globally, use the Maven executable under `tools/apache-maven-3.10.0/bin/` with the appropriate command for your operating system.

### 5. Compile and run the frontend

**Start the backend before launching the frontend.** Keep the backend terminal open while using GlucoSync.

#### Terminal 1 — Start the backend

From the project root:
Ensure that the SpringBoot backend is running.
Wait until Spring Boot starts successfully before continuing. If the backend does not start, resolve the error before launching the frontend.

#### Terminal 2 — Compile the frontend

Open a second terminal at the project root (`GlucoSync`) and compile the Java files in the AWT and Swing folders:

```powershell
javac -d Frontend Frontend/AWT/*.java Frontend/Swing/*.java
```

#### Terminal 2 — Run the homepage

After compilation succeeds, run only the GlucoSync homepage:

```powershell
java -cp Frontend GlucoSyncHome
```

The homepage's frontend API URL must point to the running backend. Keep Terminal 1 running while the application is in use.

> These commands assume the frontend classes have no additional external dependencies and that `GlucoSyncHome` has a `main()` method. If compilation reports missing classes or libraries, adjust the compile command to include the required source files and classpath.


## 🔮 Future Enhancements

-Glucose trend visualization and reporting.

-Improved appointment reminders.

-Enhanced patient monitoring dashboards.

-Additional validation and automated testing.

## 👥 Contributors
Developed as a Java-based project focused on glucose monitoring and doctor appointment management.

- Akshaya Sri https://github.com/Akshaya-Sri-Tech

- Amshita  https://github.com/amshita2647-star

- Vishnu Priya https://github.com/vp-n-887

- Aditi Renuprakash https://github.com/adit-codes

## Disclaimer

GlucoSync is an educational software project. Its alerts are intended to support monitoring and follow-up and are not a substitute for professional medical advice, diagnosis, or treatment.
