package com.opd.consultation;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
    boolean existsByAppointmentId(Long appointmentId);
    List<Consultation> findAllByAppointmentPatientIdOrderByCompletedAtDesc(Long patientId);
}
