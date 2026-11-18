package com.example.hospitalmanager.service;

import com.example.hospitalmanager.dto.PatientRequest;
import com.example.hospitalmanager.dto.PatientResponse;
import com.example.hospitalmanager.exception.ApiException;
import com.example.hospitalmanager.model.AppUser;
import com.example.hospitalmanager.model.Patient;
import com.example.hospitalmanager.model.Role;
import com.example.hospitalmanager.repository.AppointmentRepository;
import com.example.hospitalmanager.repository.MedicalRecordRepository;
import com.example.hospitalmanager.repository.PatientRepository;
import com.example.hospitalmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    @Transactional(readOnly = true)
    public List<PatientResponse> list(String search) {
        List<Patient> patients = search == null || search.isBlank()
                ? patientRepository.findAll()
                : patientRepository.findByNameContainingIgnoreCaseOrderByNameAsc(search.trim());
        return patients.stream().map(PatientResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public PatientResponse get(Long id) {
        return PatientResponse.from(find(id));
    }

    @Transactional
    public PatientResponse create(PatientRequest request) {
        Patient patient = new Patient();
        apply(patient, request);
        return PatientResponse.from(patientRepository.save(patient));
    }

    @Transactional
    public PatientResponse update(Long id, PatientRequest request) {
        Patient patient = find(id);
        apply(patient, request);
        return PatientResponse.from(patientRepository.save(patient));
    }

    @Transactional
    public void delete(Long id) {
        Patient patient = find(id);
        if (appointmentRepository.existsByPatientId(id)
                || medicalRecordRepository.existsByPatientId(id)) {
            throw new ApiException(HttpStatus.CONFLICT,
                    "Patient cannot be deleted while appointments or medical records exist");
        }
        patientRepository.delete(patient);
    }

    public Patient find(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Patient not found"));
    }

    private void apply(Patient patient, PatientRequest request) {
        patient.setName(request.name().trim());
        patient.setDateOfBirth(request.dateOfBirth());
        patient.setGender(request.gender().trim());
        patient.setPhone(request.phone().trim());
        patient.setAddress(request.address() == null ? null : request.address().trim());
        patient.setUserAccount(resolvePatientAccount(request.userId(), patient.getId()));
    }

    private AppUser resolvePatientAccount(Long userId, Long currentPatientId) {
        if (userId == null) {
            return null;
        }
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Linked user not found"));
        if (user.getRole() != Role.PATIENT) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "Only a PATIENT account can be linked to a patient profile");
        }
        patientRepository.findByUserAccountUsername(user.getUsername()).ifPresent(existing -> {
            if (currentPatientId == null || !existing.getId().equals(currentPatientId)) {
                throw new ApiException(HttpStatus.CONFLICT,
                        "This user is already linked to another patient profile");
            }
        });
        return user;
    }
}
