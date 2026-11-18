package com.example.hospitalmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PatientRequest(
        @NotBlank @Size(max = 100) String name,
        @NotNull @Past LocalDate dateOfBirth,
        @NotBlank @Size(max = 20) String gender,
        @NotBlank
        @Pattern(regexp = "^[0-9+() -]{7,20}$", message = "phone must contain 7 to 20 valid phone characters")
        String phone,
        @Size(max = 255) String address,
        Long userId
) {
}
