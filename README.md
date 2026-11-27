# careflow | Outpatient clinic

A small OPD workflow built with Angular, Spring Boot, Spring Data JPA, and MySQL. The app includes patient registration and search, appointment scheduling, visit completion with vitals and notes, and completed consultation history.

## Run the complete demo

Install Docker Desktop, then from this folder:

```powershell
docker compose up --build
```

Open [http://localhost:8081](http://localhost:8081). The REST API is available at [http://localhost:8080/api](http://localhost:8080/api). MySQL data is kept in the `opd-mysql` Docker volume.

The compose file uses local demo-only credentials (`opd` / `opd123`). Change these before using the app beyond a local demo.

To stop the services, run `docker compose down`. To remove the demo database volume as well, run `docker compose down --volumes`.

## Run frontend and backend separately

Requirements: Java 17+, Maven 3.9+, Node.js 20.19+ (or 22.12+), npm, and MySQL 8.

1. Create a MySQL database named `opd_db` and set `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, and `SPRING_DATASOURCE_PASSWORD` if your local connection differs from the defaults in `backend/src/main/resources/application.properties`.
2. In `backend`, run `mvn spring-boot:run`.
3. In `frontend`, run `npm install` once, then `npm start`.
4. Open [http://localhost:4200](http://localhost:4200). The Angular development proxy forwards `/api` requests to port 8080.

## REST endpoints

| Method | Endpoint | Purpose |
| --- | --- | --- |
| `GET` | `/api/patients?search=` | List patients; optional case-insensitive name or phone search |
| `POST` | `/api/patients` | Register a patient |
| `GET` | `/api/appointments?date=YYYY-MM-DD` | List appointments for a day; defaults to today |
| `POST` | `/api/appointments` | Book an appointment |
| `POST` | `/api/appointments/{id}/consultation` | Save vitals and notes; marks the appointment complete |
| `GET` | `/api/patients/{id}/consultations` | View a patient's completed consultation history |

Example request bodies:

```json
{ "name": "Priya Sharma", "gender": "FEMALE", "age": 32, "phoneNumber": "+91 98765 43210" }
```

```json
{ "patientId": 1, "doctorName": "Dr. Ananya Rao", "appointmentAt": "2026-11-27T16:00:00" }
```

```json
{ "bloodPressure": "120/80", "temperature": 36.8, "notes": "Mild seasonal allergy. Rest and follow up in one week." }
```

## Functional flow

Register a patient in **Patients**. Book a visit in **Appointments**, selecting the patient and entering the doctor and date/time. Choose **Start visit** to record blood pressure, temperature, and clinical notes; saving the consultation also marks the appointment complete. The **Consultations** screen lets you select a patient and review completed visits.

## Project layout

- `frontend/` — Angular standalone app and responsive clinic dashboard.
- `backend/` — Spring Boot REST API, validation, JPA entities, and repositories.
- `docker-compose.yml` — MySQL, API, and web application for the local demo.

This is a demo application, not a production-ready medical records system. It intentionally does not include authentication, authorization, audit controls, or production secret management.
