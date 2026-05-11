package com.hospital.app.repository;

import com.hospital.app.entity.Doctor;
import com.hospital.app.entity.Patient;
import com.hospital.app.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {
    List<Visit> findByPatient(Patient patient);
    List<Visit> findByDoctor(Doctor doctor);
    List<Visit> findByPatientOrderByVisitDateDesc(Patient patient);
}
