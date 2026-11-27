package com.opd.appointment;

import com.opd.patient.Patient;
import com.opd.patient.PatientRepository;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8081"})
public class AppointmentController {
    private final AppointmentRepository appointments;
    private final PatientRepository patients;

    public AppointmentController(AppointmentRepository appointments, PatientRepository patients) {
        this.appointments = appointments;
        this.patients = patients;
    }

    @GetMapping
    public List<Appointment> list(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate day = date == null ? LocalDate.now() : date;
        return appointments.findAllByAppointmentAtGreaterThanEqualAndAppointmentAtLessThanOrderByAppointmentAtAsc(
                day.atStartOfDay(), day.plusDays(1).atStartOfDay());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Appointment create(@Valid @RequestBody AppointmentRequest request) {
        Patient patient = patients.findById(request.patientId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found"));
        return appointments.save(new Appointment(patient, request.doctorName().trim(), request.appointmentAt()));
    }
}
