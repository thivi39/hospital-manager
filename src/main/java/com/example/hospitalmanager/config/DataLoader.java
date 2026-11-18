package com.example.hospitalmanager.config;

import com.example.hospitalmanager.model.AppUser;
import com.example.hospitalmanager.model.Patient;
import com.example.hospitalmanager.model.Role;
import com.example.hospitalmanager.repository.PatientRepository;
import com.example.hospitalmanager.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner seedData(UserRepository userRepository,
                               PatientRepository patientRepository,
                               PasswordEncoder passwordEncoder) {
        return args -> {
            createUserIfMissing(userRepository, passwordEncoder,
                    "System Administrator", "admin", "Admin@123", Role.ADMIN);
            createUserIfMissing(userRepository, passwordEncoder,
                    "Dr. Nimal Perera", "doctor", "Doctor@123", Role.DOCTOR);

            if (patientRepository.count() == 0) {
                patientRepository.save(Patient.builder()
                        .name("Kamal Silva")
                        .dateOfBirth(LocalDate.of(1990, 4, 12))
                        .gender("Male")
                        .phone("0771234567")
                        .address("Colombo")
                        .build());
                patientRepository.save(Patient.builder()
                        .name("Nimali Perera")
                        .dateOfBirth(LocalDate.of(1996, 8, 22))
                        .gender("Female")
                        .phone("0712345678")
                        .address("Kandy")
                        .build());
            }
        };
    }

    private void createUserIfMissing(UserRepository userRepository,
                                     PasswordEncoder passwordEncoder,
                                     String name, String username,
                                     String password, Role role) {
        if (!userRepository.existsByUsername(username)) {
            userRepository.save(AppUser.builder()
                    .name(name)
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .role(role)
                    .build());
        }
    }
}
