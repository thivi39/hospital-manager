package com.example.hospitalmanager.controller;

import com.example.hospitalmanager.dto.PatientResponse;
import com.example.hospitalmanager.dto.RegisterRequest;
import com.example.hospitalmanager.dto.UserResponse;
import com.example.hospitalmanager.exception.ApiException;
import com.example.hospitalmanager.model.Role;
import com.example.hospitalmanager.service.AuthService;
import com.example.hospitalmanager.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final PatientService patientService;
    private final AuthService authService;

    @GetMapping("/patients")
    public List<PatientResponse> patients() {
        return patientService.list(null);
    }

    @PostMapping("/doctors")
    public ResponseEntity<UserResponse> addDoctor(
            @Valid @RequestBody RegisterRequest request) {
        if (request.role() != Role.DOCTOR) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "This endpoint creates DOCTOR accounts only");
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.createStaff(request));
    }
}
