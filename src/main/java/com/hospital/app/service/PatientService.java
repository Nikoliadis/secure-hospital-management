package com.hospital.app.service;

import com.hospital.app.entity.Patient;
import com.hospital.app.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {

    private static final Logger log = LoggerFactory.getLogger(PatientService.class);

    private final PatientRepository patientRepository;

    public List<Patient> findAll() {
        return patientRepository.findAll();
    }

    public Patient findById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found: " + id));
    }

    @Transactional
    public Patient save(Patient patient) {
        if (patient.getId() == null) {
            if (patientRepository.existsByAmka(patient.getAmka())) {
                throw new IllegalArgumentException("AMKA already registered: " + patient.getAmka());
            }
            log.info("AUDIT: New patient created - amka={}", patient.getAmka());
        } else {
            log.info("AUDIT: Patient updated - id={}", patient.getId());
        }
        return patientRepository.save(patient);
    }

    @Transactional
    public void delete(Long id) {
        log.info("AUDIT: Patient deleted - id={}", id);
        patientRepository.deleteById(id);
    }
}
