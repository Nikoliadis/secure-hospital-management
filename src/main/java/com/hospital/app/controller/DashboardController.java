package com.hospital.app.controller;

import com.hospital.app.entity.Role;
import com.hospital.app.entity.User;
import com.hospital.app.repository.UserRepository;
import com.hospital.app.service.DoctorService;
import com.hospital.app.service.PatientService;
import com.hospital.app.service.VisitService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final UserRepository userRepository;
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final VisitService visitService;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow();

        model.addAttribute("user", user);
        model.addAttribute("totalPatients", patientService.findAll().size());
        model.addAttribute("totalDoctors", doctorService.findAll().size());
        model.addAttribute("totalVisits", visitService.findAll().size());

        if (user.getRole() == Role.DOCTOR) {
            doctorService.findByUser(user).ifPresent(doctor ->
                model.addAttribute("myVisits", visitService.findByDoctor(doctor))
            );
        }

        return "dashboard";
    }
}
