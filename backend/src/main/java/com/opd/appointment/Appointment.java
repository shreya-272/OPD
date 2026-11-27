package com.opd.appointment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.opd.patient.Patient;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", nullable = false)
    @JsonIgnoreProperties({"createdAt"})
    private Patient patient;

    @Column(nullable = false, length = 120)
    private String doctorName;

    @Column(nullable = false)
    private LocalDateTime appointmentAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AppointmentStatus status = AppointmentStatus.SCHEDULED;

    protected Appointment() {}

    public Appointment(Patient patient, String doctorName, LocalDateTime appointmentAt) {
        this.patient = patient;
        this.doctorName = doctorName;
        this.appointmentAt = appointmentAt;
    }

    public Long getId() { return id; }
    public Patient getPatient() { return patient; }
    public String getDoctorName() { return doctorName; }
    public LocalDateTime getAppointmentAt() { return appointmentAt; }
    public AppointmentStatus getStatus() { return status; }
    public void complete() { status = AppointmentStatus.COMPLETED; }
}
