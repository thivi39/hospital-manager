package com.example.hospitalmanager.service;

import com.example.hospitalmanager.dto.MedicalRecordRequest;
import com.example.hospitalmanager.dto.MedicalRecordResponse;
import com.example.hospitalmanager.exception.ApiException;
import com.example.hospitalmanager.model.AppUser;
import com.example.hospitalmanager.model.MedicalRecord;
import com.example.hospitalmanager.model.Patient;
import com.example.hospitalmanager.model.Role;
import com.example.hospitalmanager.repository.MedicalRecordRepository;
import com.example.hospitalmanager.repository.PatientRepository;
import com.example.hospitalmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    @Transactional
    public MedicalRecordResponse create(MedicalRecordRequest request,
                                        Authentication authentication) {
        AppUser doctor = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED,
                        "Authenticated user was not found"));
        if (doctor.getRole() != Role.DOCTOR) {
            throw new ApiException(HttpStatus.FORBIDDEN,
                    "Only doctors can write medical records");
        }
        Patient patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Patient not found"));
        MedicalRecord record = MedicalRecord.builder()
                .patient(patient)
                .doctor(doctor)
                .diagnosis(request.diagnosis().trim())
                .prescription(request.prescription().trim())
                .notes(request.notes() == null ? null : request.notes().trim())
                .createdAt(LocalDateTime.now())
                .build();
        return MedicalRecordResponse.from(medicalRecordRepository.save(record));
    }

    @Transactional(readOnly = true)
    public List<MedicalRecordResponse> listForPatient(Long patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Patient not found");
        }
        return medicalRecordRepository.findByPatientIdOrderByCreatedAtDesc(patientId)
                .stream().map(MedicalRecordResponse::from).toList();
    }
}
