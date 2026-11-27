package com.opd.appointment;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public record AppointmentRequest(
        @NotNull @Positive Long patientId,
        @NotBlank @Size(max = 120) String doctorName,
        @NotNull @FutureOrPresent LocalDateTime appointmentAt
) {}
