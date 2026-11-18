package com.example.hospitalmanager.service;

import com.example.hospitalmanager.dto.AuthResponse;
import com.example.hospitalmanager.dto.LoginRequest;
import com.example.hospitalmanager.dto.RegisterRequest;
import com.example.hospitalmanager.dto.UserResponse;
import com.example.hospitalmanager.exception.ApiException;
import com.example.hospitalmanager.model.AppUser;
import com.example.hospitalmanager.model.Role;
import com.example.hospitalmanager.repository.UserRepository;
import com.example.hospitalmanager.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    public UserResponse registerPatient(RegisterRequest request) {
        if (request.role() != Role.PATIENT) {
            throw new ApiException(HttpStatus.FORBIDDEN,
                    "Public registration is limited to PATIENT accounts");
        }
        return createUser(request);
    }

    @Transactional
    public UserResponse createStaff(RegisterRequest request) {
        return createUser(request);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.username(), request.password()));
        AppUser user = findByUsername(request.username());
        return new AuthResponse(
                jwtService.generateToken(user),
                "Bearer",
                jwtService.getExpirationSeconds(),
                user.getRole()
        );
    }

    @Transactional(readOnly = true)
    public UserResponse currentUser(Authentication authentication) {
        return UserResponse.from(findByUsername(authentication.getName()));
    }

    @Transactional(readOnly = true)
    public List<UserResponse> listUsers() {
        return userRepository.findAll().stream().map(UserResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<UserResponse> listDoctors() {
        return userRepository.findAllByRoleOrderByNameAsc(Role.DOCTOR)
                .stream().map(UserResponse::from).toList();
    }

    public AppUser findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private UserResponse createUser(RegisterRequest request) {
        String username = request.username().trim();
        if (userRepository.existsByUsername(username)) {
            throw new ApiException(HttpStatus.CONFLICT, "Username is already registered");
        }
        AppUser user = AppUser.builder()
                .name(request.name().trim())
                .username(username)
                .password(passwordEncoder.encode(request.password()))
                .role(request.role())
                .build();
        return UserResponse.from(userRepository.save(user));
    }
}
