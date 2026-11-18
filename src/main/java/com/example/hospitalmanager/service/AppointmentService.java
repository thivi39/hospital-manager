package com.example.hospitalmanager.service;

import com.example.hospitalmanager.dto.AppointmentRequest;
import com.example.hospitalmanager.dto.AppointmentResponse;
import com.example.hospitalmanager.dto.StatusRequest;
import com.example.hospitalmanager.exception.ApiException;
import com.example.hospitalmanager.model.AppUser;
import com.example.hospitalmanager.model.Appointment;
import com.example.hospitalmanager.model.AppointmentStatus;
import com.example.hospitalmanager.model.Patient;
import com.example.hospitalmanager.model.Role;
import com.example.hospitalmanager.repository.AppointmentRepository;
import com.example.hospitalmanager.repository.PatientRepository;
import com.example.hospitalmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    @Transactional
    public AppointmentResponse book(AppointmentRequest request, Authentication authentication) {
        Patient patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Patient not found"));
        AppUser doctor = userRepository.findById(request.doctorId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Doctor not found"));
        if (doctor.getRole() != Role.DOCTOR) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Selected user is not a doctor");
        }

        AppUser currentUser = currentUser(authentication);
        if (currentUser.getRole() == Role.PATIENT
                && (patient.getUserAccount() == null
                || !patient.getUserAccount().getUsername().equals(currentUser.getUsername()))) {
            throw new ApiException(HttpStatus.FORBIDDEN,
                    "Patients can book appointments only for their own profile");
        }

        Appointment appointment = Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .appointmentDateTime(request.appointmentDateTime())
                .reason(request.reason().trim())
                .status(AppointmentStatus.SCHEDULED)
                .build();
        return AppointmentResponse.from(appointmentRepository.save(appointment));
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> list(Authentication authentication) {
        AppUser currentUser = currentUser(authentication);
        List<Appointment> appointments = switch (currentUser.getRole()) {
            case DOCTOR -> appointmentRepository
                    .findByDoctorUsernameOrderByAppointmentDateTimeAsc(currentUser.getUsername());
            case PATIENT -> appointmentRepository
                    .findByPatientUserAccountUsernameOrderByAppointmentDateTimeAsc(
                            currentUser.getUsername());
            default -> appointmentRepository.findAllByOrderByAppointmentDateTimeAsc();
        };
        return appointments.stream().map(AppointmentResponse::from).toList();
    }

    @Transactional
    public AppointmentResponse changeStatus(Long id, StatusRequest request,
                                            Authentication authentication) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                        "Appointment not found"));
        AppUser currentUser = currentUser(authentication);
        if (currentUser.getRole() == Role.DOCTOR
                && !appointment.getDoctor().getUsername().equals(currentUser.getUsername())) {
            throw new ApiException(HttpStatus.FORBIDDEN,
                    "Doctors can update only their own appointments");
        }
        appointment.setStatus(request.status());
        return AppointmentResponse.from(appointmentRepository.save(appointment));
    }

    private AppUser currentUser(Authentication authentication) {
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED,
                        "Authenticated user was not found"));
    }
}
