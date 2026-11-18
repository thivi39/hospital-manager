package com.example.hospitalmanager.dto;

import com.example.hospitalmanager.model.Patient;

import java.time.LocalDate;

public record PatientResponse(
        Long id,
        String name,
        LocalDate dateOfBirth,
        String gender,
        String phone,
        String address,
        String username
) {
    public static PatientResponse from(Patient patient) {
        String username = patient.getUserAccount() == null
                ? null
                : patient.getUserAccount().getUsername();
        return new PatientResponse(patient.getId(), patient.getName(), patient.getDateOfBirth(),
                patient.getGender(), patient.getPhone(), patient.getAddress(), username);
    }
}
