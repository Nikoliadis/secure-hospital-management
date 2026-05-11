package com.hospital.app.controller;

import com.hospital.app.dto.VisitForm;
import com.hospital.app.entity.Doctor;
import com.hospital.app.entity.Role;
import com.hospital.app.entity.User;
import com.hospital.app.entity.Visit;
import com.hospital.app.repository.UserRepository;
import com.hospital.app.service.DoctorService;
import com.hospital.app.service.PatientService;
import com.hospital.app.service.VisitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/visits")
@RequiredArgsConstructor
public class VisitController {

    private static final Logger log = LoggerFactory.getLogger(VisitController.class);

    private final VisitService visitService;
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final UserRepository userRepository;

    @GetMapping
    public String list(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        if (user.getRole() == Role.DOCTOR) {
            doctorService.findByUser(user).ifPresent(d ->
                model.addAttribute("visits", visitService.findByDoctor(d))
            );
        } else {
            model.addAttribute("visits", visitService.findAll());
        }
        return "visits/list";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id,
                       @AuthenticationPrincipal UserDetails userDetails,
                       Model model) {
        Visit visit = visitService.findById(id);
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();

        boolean isAttendingDoctor = user.getRole() == Role.DOCTOR &&
                doctorService.findByUser(user)
                        .map(d -> d.getId().equals(visit.getDoctor().getId()))
                        .orElse(false);
        boolean canSeeDiagnosis = user.getRole() == Role.ADMIN || isAttendingDoctor;

        model.addAttribute("visit", visit);
        model.addAttribute("canSeeDiagnosis", canSeeDiagnosis);
        if (canSeeDiagnosis) {
            model.addAttribute("diagnosis", visitService.getDecryptedDiagnosis(visit));
        }
        return "visits/view";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public String newForm(Model model) {
        model.addAttribute("visitForm", new VisitForm());
        model.addAttribute("patients", patientService.findAll());
        model.addAttribute("doctors", doctorService.findAll());
        return "visits/form";
    }

    @PostMapping("/new")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public String create(@Valid @ModelAttribute VisitForm form,
                         BindingResult result,
                         @AuthenticationPrincipal UserDetails userDetails,
                         RedirectAttributes ra,
                         Model model) {
        if (result.hasErrors()) {
            model.addAttribute("patients", patientService.findAll());
            model.addAttribute("doctors", doctorService.findAll());
            return "visits/form";
        }
        Visit visit = toVisit(form);
        visitService.save(visit, form.getDiagnosis());
        log.info("AUDIT: Visit created by {}", userDetails.getUsername());
        ra.addFlashAttribute("success", "Visit registered successfully");
        return "redirect:/visits";
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public String editForm(@PathVariable Long id,
                           @AuthenticationPrincipal UserDetails userDetails,
                           Model model) {
        Visit visit = visitService.findById(id);
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        ensureEditPermission(user, visit);

        VisitForm form = toForm(visit);
        form.setDiagnosis(visitService.getDecryptedDiagnosis(visit));

        model.addAttribute("visitForm", form);
        model.addAttribute("visitId", id);
        model.addAttribute("patients", patientService.findAll());
        model.addAttribute("doctors", doctorService.findAll());
        return "visits/form";
    }

    @PostMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute VisitForm form,
                         BindingResult result,
                         @AuthenticationPrincipal UserDetails userDetails,
                         RedirectAttributes ra,
                         Model model) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        Visit existing = visitService.findById(id);
        ensureEditPermission(user, existing);

        if (result.hasErrors()) {
            model.addAttribute("visitId", id);
            model.addAttribute("patients", patientService.findAll());
            model.addAttribute("doctors", doctorService.findAll());
            return "visits/form";
        }
        Visit visit = toVisit(form);
        visit.setId(id);
        visit.setCreatedAt(existing.getCreatedAt());
        visitService.save(visit, form.getDiagnosis());
        log.info("AUDIT: Visit {} updated by {}", id, userDetails.getUsername());
        ra.addFlashAttribute("success", "Visit updated successfully");
        return "redirect:/visits";
    }

    private Visit toVisit(VisitForm form) {
        Visit visit = new Visit();
        visit.setPatient(patientService.findById(form.getPatientId()));
        visit.setDoctor(doctorService.findById(form.getDoctorId()));
        visit.setVisitDate(form.getVisitDate());
        visit.setReason(form.getReason());
        visit.setNotes(form.getNotes());
        return visit;
    }

    private VisitForm toForm(Visit visit) {
        VisitForm form = new VisitForm();
        form.setPatientId(visit.getPatient().getId());
        form.setDoctorId(visit.getDoctor().getId());
        form.setVisitDate(visit.getVisitDate());
        form.setReason(visit.getReason());
        form.setNotes(visit.getNotes());
        return form;
    }

    private void ensureEditPermission(User user, Visit visit) {
        if (user.getRole() == Role.ADMIN) return;
        boolean isOwner = doctorService.findByUser(user)
                .map(d -> d.getId().equals(visit.getDoctor().getId()))
                .orElse(false);
        if (!isOwner) {
            log.warn("AUDIT: Unauthorized visit edit attempt by {}", user.getUsername());
            throw new AccessDeniedException("You can only edit your own visits");
        }
    }
}
