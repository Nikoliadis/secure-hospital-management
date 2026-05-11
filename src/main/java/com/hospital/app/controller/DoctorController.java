package com.hospital.app.controller;

import com.hospital.app.entity.Doctor;
import com.hospital.app.service.DoctorService;
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
@RequestMapping("/doctors")
@PreAuthorize("hasAnyRole('ADMIN','SECRETARIAT')")
@RequiredArgsConstructor
public class DoctorController {

    private static final Logger log = LoggerFactory.getLogger(DoctorController.class);

    private final DoctorService doctorService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("doctors", doctorService.findAll());
        return "doctors/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String newForm(Model model) {
        model.addAttribute("doctor", new Doctor());
        return "doctors/form";
    }

    @PostMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String create(@Valid @ModelAttribute Doctor doctor,
                         BindingResult result,
                         RedirectAttributes ra,
                         Principal principal) {
        if (result.hasErrors()) {
            return "doctors/form";
        }
        doctorService.save(doctor);
        log.info("AUDIT: Doctor created by {}", principal.getName());
        ra.addFlashAttribute("success", "Doctor created successfully");
        return "redirect:/doctors";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("doctor", doctorService.findById(id));
        return "doctors/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute Doctor doctor,
                         BindingResult result,
                         RedirectAttributes ra,
                         Principal principal) {
        if (result.hasErrors()) {
            return "doctors/form";
        }
        doctor.setId(id);
        doctorService.save(doctor);
        log.info("AUDIT: Doctor {} updated by {}", id, principal.getName());
        ra.addFlashAttribute("success", "Doctor updated successfully");
        return "redirect:/doctors";
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id, RedirectAttributes ra, Principal principal) {
        doctorService.delete(id);
        log.info("AUDIT: Doctor {} deleted by {}", id, principal.getName());
        ra.addFlashAttribute("success", "Doctor deleted");
        return "redirect:/doctors";
    }
}
