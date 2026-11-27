package com.opd.consultation;

import jakarta.validation.constraints.*;

public record ConsultationRequest(
        @NotBlank @Pattern(regexp = "\\d{2,3}/\\d{2,3}", message = "Use systolic/diastolic format, e.g. 120/80")
        String bloodPressure,
        @NotNull @DecimalMin("25.0") @DecimalMax("45.0") Double temperature,
        @NotBlank @Size(max = 1000) String notes
) {}
