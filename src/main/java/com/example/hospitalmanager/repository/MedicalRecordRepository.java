package com.example.hospitalmanager.repository;

import com.example.hospitalmanager.model.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {

    List<MedicalRecord> findByPatientIdOrderByCreatedAtDesc(Long patientId);

    boolean existsByPatientId(Long patientId);
}
