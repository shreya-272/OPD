package com.opd.patient;

import jakarta.validation.constraints.*;

public record PatientRequest(
        @NotBlank @Size(max = 120) String name,
        @NotNull Gender gender,
        @NotNull @Min(0) @Max(125) Integer age,
        @NotBlank @Pattern(regexp = "[0-9+() .-]{7,20}", message = "Enter a valid phone number") String phoneNumber
) {}
