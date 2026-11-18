package com.example.hospitalmanager.dto;

import com.example.hospitalmanager.model.MedicalRecord;

import java.time.LocalDateTime;

public record MedicalRecordResponse(
        Long id,
        Long patientId,
        String patientName,
        Long doctorId,
        String doctorName,
        String diagnosis,
        String prescription,
        String notes,
        LocalDateTime createdAt
) {
    public static MedicalRecordResponse from(MedicalRecord record) {
        return new MedicalRecordResponse(
                record.getId(),
                record.getPatient().getId(),
                record.getPatient().getName(),
                record.getDoctor().getId(),
                record.getDoctor().getName(),
                record.getDiagnosis(),
                record.getPrescription(),
                record.getNotes(),
                record.getCreatedAt()
        );
    }
}
