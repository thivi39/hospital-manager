package com.example.hospitalmanager.dto;

import com.example.hospitalmanager.model.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Size(max = 100) String username,
        @NotBlank @Size(min = 8, max = 100) String password,
        @NotNull Role role
) {
}
