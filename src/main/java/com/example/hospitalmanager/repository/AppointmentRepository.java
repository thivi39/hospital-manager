package com.example.hospitalmanager.repository;

import com.example.hospitalmanager.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findAllByOrderByAppointmentDateTimeAsc();

    List<Appointment> findByDoctorIdOrderByAppointmentDateTimeAsc(Long doctorId);

    List<Appointment> findByDoctorUsernameOrderByAppointmentDateTimeAsc(String username);

    List<Appointment> findByPatientUserAccountUsernameOrderByAppointmentDateTimeAsc(String username);

    boolean existsByPatientId(Long patientId);
}
