package com.hospital.app.controller;

import com.hospital.app.dto.CreateUserForm;
import com.hospital.app.entity.Role;
import com.hospital.app.entity.User;
import com.hospital.app.repository.UserRepository;
import com.hospital.app.security.PasswordValidator;
import com.hospital.app.service.UserService;
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
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordValidator passwordValidator;

    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "admin/users";
    }

    @GetMapping("/users/new")
    public String newUserForm(Model model) {
        model.addAttribute("form", new CreateUserForm());
        model.addAttribute("roles", Role.values());
        return "admin/user-form";
    }

    @PostMapping("/users/new")
    public String createUser(@Valid @ModelAttribute("form") CreateUserForm form,
                             BindingResult result,
                             RedirectAttributes ra,
                             Principal principal,
                             Model model) {
        if (!passwordValidator.isValid(form.getPassword())) {
            result.rejectValue("password", "invalid", passwordValidator.getRequirements());
        }
        if (result.hasErrors()) {
            model.addAttribute("roles", Role.values());
            return "admin/user-form";
        }
        try {
            userService.createUser(form.getUsername(), form.getPassword(), form.getEmail(), form.getRole());
            log.info("AUDIT: User created by admin {} - new user: {}", principal.getName(), form.getUsername());
            ra.addFlashAttribute("success", "User '" + form.getUsername() + "' created successfully");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/toggle-lock")
    public String toggleLock(@PathVariable Long id, RedirectAttributes ra, Principal principal) {
        User user = userService.findById(id);
        user.setAccountNonLocked(!user.isAccountNonLocked());
        userRepository.save(user);
        String status = user.isAccountNonLocked() ? "unlocked" : "locked";
        log.info("AUDIT: User {} {} by admin {}", user.getUsername(), status, principal.getName());
        ra.addFlashAttribute("success", "User '" + user.getUsername() + "' " + status);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes ra, Principal principal) {
        User user = userService.findById(id);
        String username = user.getUsername();
        userService.deleteUser(id);
        log.info("AUDIT: User {} deleted by admin {}", username, principal.getName());
        ra.addFlashAttribute("success", "User '" + username + "' deleted");
        return "redirect:/admin/users";
    }
}
