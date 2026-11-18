package com.example.hospitalmanager.controller;

import com.example.hospitalmanager.dto.AppointmentRequest;
import com.example.hospitalmanager.dto.AppointmentResponse;
import com.example.hospitalmanager.dto.StatusRequest;
import com.example.hospitalmanager.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping({"/appointments", "/doctors/appointments"})
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','NURSE','RECEPTIONIST','PATIENT')")
    public List<AppointmentResponse> list(Authentication authentication) {
        return appointmentService.list(authentication);
    }

    @PostMapping({"/appointments", "/patients/appointments"})
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','PATIENT')")
    public ResponseEntity<AppointmentResponse> book(
            @Valid @RequestBody AppointmentRequest request,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(appointmentService.book(request, authentication));
    }

    @PatchMapping("/appointments/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public AppointmentResponse changeStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusRequest request,
            Authentication authentication) {
        return appointmentService.changeStatus(id, request, authentication);
    }
}
