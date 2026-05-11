package com.hospital.app.service;

import com.hospital.app.entity.Doctor;
import com.hospital.app.entity.Patient;
import com.hospital.app.entity.Visit;
import com.hospital.app.repository.VisitRepository;
import com.hospital.app.util.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VisitService {

    private static final Logger log = LoggerFactory.getLogger(VisitService.class);

    private final VisitRepository visitRepository;
    private final EncryptionUtil encryptionUtil;

    public List<Visit> findAll() {
        return visitRepository.findAll();
    }

    public List<Visit> findByPatient(Patient patient) {
        return visitRepository.findByPatientOrderByVisitDateDesc(patient);
    }

    public List<Visit> findByDoctor(Doctor doctor) {
        return visitRepository.findByDoctor(doctor);
    }

    public Visit findById(Long id) {
        return visitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Visit not found: " + id));
    }

    @Transactional
    public Visit save(Visit visit, String plainDiagnosis) {
        if (plainDiagnosis != null && !plainDiagnosis.isBlank()) {
            visit.setDiagnosisEncrypted(encryptionUtil.encrypt(plainDiagnosis));
        }
        if (visit.getId() == null) {
            log.info("AUDIT: New visit registered - patientId={}, doctorId={}",
                    visit.getPatient().getId(), visit.getDoctor().getId());
        } else {
            log.info("AUDIT: Visit updated - id={}", visit.getId());
        }
        return visitRepository.save(visit);
    }

    public String getDecryptedDiagnosis(Visit visit) {
        return encryptionUtil.decrypt(visit.getDiagnosisEncrypted());
    }

    @Transactional
    public void delete(Long id) {
        log.info("AUDIT: Visit deleted - id={}", id);
        visitRepository.deleteById(id);
    }
}
