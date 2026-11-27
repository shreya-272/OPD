package com.opd.consultation;

import com.opd.appointment.Appointment;
import com.opd.appointment.AppointmentRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8081"})
public class ConsultationController {
    private final AppointmentRepository appointments;
    private final ConsultationRepository consultations;

    public ConsultationController(AppointmentRepository appointments, ConsultationRepository consultations) {
        this.appointments = appointments;
        this.consultations = consultations;
    }

    @PostMapping("/appointments/{appointmentId}/consultation")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public Consultation complete(
            @PathVariable Long appointmentId, @Valid @RequestBody ConsultationRequest request) {
        Appointment appointment = appointments.findById(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));
        if (consultations.existsByAppointmentId(appointmentId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Consultation is already complete");
        }
        appointment.complete();
        appointments.save(appointment);
        return consultations.save(new Consultation(
                appointment, request.bloodPressure().trim(), request.temperature(), request.notes().trim()));
    }

    @GetMapping("/patients/{patientId}/consultations")
    public List<Consultation> listForPatient(@PathVariable Long patientId) {
        return consultations.findAllByAppointmentPatientIdOrderByCompletedAtDesc(patientId);
    }
}
