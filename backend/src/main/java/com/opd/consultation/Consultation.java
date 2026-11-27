package com.opd.consultation;

import com.opd.appointment.Appointment;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "consultations")
public class Consultation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "appointment_id", nullable = false, unique = true)
    private Appointment appointment;

    @Column(nullable = false, length = 40)
    private String bloodPressure;

    @Column(nullable = false)
    private Double temperature;

    @Column(nullable = false, length = 1000)
    private String notes;

    @Column(nullable = false, updatable = false)
    private LocalDateTime completedAt;

    protected Consultation() {}

    public Consultation(Appointment appointment, String bloodPressure, Double temperature, String notes) {
        this.appointment = appointment;
        this.bloodPressure = bloodPressure;
        this.temperature = temperature;
        this.notes = notes;
    }

    @PrePersist
    void onCreate() {
        completedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Appointment getAppointment() { return appointment; }
    public String getBloodPressure() { return bloodPressure; }
    public Double getTemperature() { return temperature; }
    public String getNotes() { return notes; }
    public LocalDateTime getCompletedAt() { return completedAt; }
}
