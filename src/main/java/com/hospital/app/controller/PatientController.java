package com.hospital.app.controller;

import com.hospital.app.entity.Patient;
import com.hospital.app.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/patients")
@PreAuthorize("hasAnyRole('ADMIN','SECRETARIAT')")
@RequiredArgsConstructor
public class PatientController {

    private static final Logger log = LoggerFactory.getLogger(PatientController.class);

    private final PatientService patientService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("patients", patientService.findAll());
        return "patients/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("patient", new Patient());
        return "patients/form";
    }

    @PostMapping("/new")
    public String create(@Valid @ModelAttribute Patient patient,
                         BindingResult result,
                         RedirectAttributes ra,
                         Principal principal) {
        if (result.hasErrors()) {
            return "patients/form";
        }
        try {
            patientService.save(patient);
            log.info("AUDIT: Patient created by {}", principal.getName());
            ra.addFlashAttribute("success", "Patient created successfully");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/patients";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("patient", patientService.findById(id));
        return "patients/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute Patient patient,
                         BindingResult result,
                         RedirectAttributes ra,
                         Principal principal) {
        if (result.hasErrors()) {
            return "patients/form";
        }
        patient.setId(id);
        patientService.save(patient);
        log.info("AUDIT: Patient {} updated by {}", id, principal.getName());
        ra.addFlashAttribute("success", "Patient updated successfully");
        return "redirect:/patients";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        model.addAttribute("patient", patientService.findById(id));
        return "patients/view";
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id, RedirectAttributes ra, Principal principal) {
        patientService.delete(id);
        log.info("AUDIT: Patient {} deleted by {}", id, principal.getName());
        ra.addFlashAttribute("success", "Patient deleted");
        return "redirect:/patients";
    }
}
