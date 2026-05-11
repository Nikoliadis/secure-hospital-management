package com.hospital.app.security;

import com.hospital.app.entity.*;
import com.hospital.app.repository.*;
import com.hospital.app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserService userService;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Override
    public void run(String... args) {
        createUsers();
        createDoctorsAndPatients();
        log.info("AUDIT: Database initialized with seed data");
    }

    private void createUsers() {
        User admin   = userService.createUser("admin",       "Admin@1234!",  "admin@hospital.gr",       Role.ADMIN);
        User doctor1 = userService.createUser("dr.smith",   "Doctor@1234!", "smith@hospital.gr",        Role.DOCTOR);
        User doctor2 = userService.createUser("dr.jones",   "Doctor@1234!", "jones@hospital.gr",        Role.DOCTOR);
        User secr    = userService.createUser("secretary",  "Secr@1234!",   "secretary@hospital.gr",    Role.SECRETARIAT);
        User patient1User = userService.createUser("patient1", "Patient@123!", "patient1@email.com",    Role.PATIENT);

        createDoctor(doctor1, "John",  "Smith", "Cardiology",  "CARD001", "2101234567");
        createDoctor(doctor2, "Emily", "Jones", "Neurology",   "NEUR001", "2109876543");
        createPatient(patient1User, "George", "Papadopoulos", "01010185001", LocalDate.of(1985, 1, 1), "6941234567");
    }

    private void createDoctor(User user, String firstName, String lastName, String specialty, String license, String phone) {
        Doctor doctor = new Doctor();
        doctor.setFirstName(firstName);
        doctor.setLastName(lastName);
        doctor.setSpecialty(specialty);
        doctor.setLicenseNumber(license);
        doctor.setPhone(phone);
        doctor.setUser(user);
        doctorRepository.save(doctor);
    }

    private void createPatient(User user, String firstName, String lastName, String amka, LocalDate dob, String phone) {
        Patient patient = new Patient();
        patient.setFirstName(firstName);
        patient.setLastName(lastName);
        patient.setAmka(amka);
        patient.setDateOfBirth(dob);
        patient.setPhone(phone);
        patient.setUser(user);
        patientRepository.save(patient);
    }

    private void createDoctorsAndPatients() {
        // Extra sample patients (no linked user account)
        Patient p2 = new Patient();
        p2.setFirstName("Maria"); p2.setLastName("Georgiou");
        p2.setAmka("02020290002"); p2.setDateOfBirth(LocalDate.of(1990, 2, 2));
        p2.setPhone("6951234567");
        patientRepository.save(p2);
    }
}
