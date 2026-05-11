package com.hospital.app.controller;

import com.hospital.app.entity.Patient;
import com.hospital.app.entity.User;
import com.hospital.app.entity.Visit;
import com.hospital.app.service.PatientService;
import com.hospital.app.service.UserService;
import com.hospital.app.service.VisitService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminRestController {

    private static final Logger log = LoggerFactory.getLogger(AdminRestController.class);

    private final UserService userService;
    private final PatientService patientService;
    private final VisitService visitService;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers(Principal principal) {
        log.info("AUDIT: REST /api/admin/users accessed by {}", principal.getName());
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id, Principal principal) {
        log.info("AUDIT: REST /api/admin/users/{} accessed by {}", id, principal.getName());
        return ResponseEntity.ok(userService.findById(id));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id, Principal principal) {
        log.info("AUDIT: REST DELETE /api/admin/users/{} by {}", id, principal.getName());
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/patients")
    public ResponseEntity<List<Patient>> getAllPatients(Principal principal) {
        log.info("AUDIT: REST /api/admin/patients accessed by {}", principal.getName());
        return ResponseEntity.ok(patientService.findAll());
    }

    @GetMapping("/visits")
    public ResponseEntity<List<Visit>> getAllVisits(Principal principal) {
        log.info("AUDIT: REST /api/admin/visits accessed by {}", principal.getName());
        return ResponseEntity.ok(visitService.findAll());
    }
}
