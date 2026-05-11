package com.hospital.app.service;

import com.hospital.app.entity.Doctor;
import com.hospital.app.entity.User;
import com.hospital.app.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private static final Logger log = LoggerFactory.getLogger(DoctorService.class);

    private final DoctorRepository doctorRepository;

    public List<Doctor> findAll() {
        return doctorRepository.findAll();
    }

    public Doctor findById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found: " + id));
    }

    public Optional<Doctor> findByUser(User user) {
        return doctorRepository.findByUser(user);
    }

    @Transactional
    public Doctor save(Doctor doctor) {
        if (doctor.getId() == null) {
            log.info("AUDIT: New doctor created - license={}", doctor.getLicenseNumber());
        } else {
            log.info("AUDIT: Doctor updated - id={}", doctor.getId());
        }
        return doctorRepository.save(doctor);
    }

    @Transactional
    public void delete(Long id) {
        log.info("AUDIT: Doctor deleted - id={}", id);
        doctorRepository.deleteById(id);
    }
}
