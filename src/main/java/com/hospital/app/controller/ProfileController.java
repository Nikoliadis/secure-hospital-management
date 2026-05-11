package com.hospital.app.controller;

import com.hospital.app.repository.UserRepository;
import com.hospital.app.security.PasswordValidator;
import com.hospital.app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordValidator passwordValidator;

    @GetMapping
    public String profile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        model.addAttribute("user", userRepository.findByUsername(userDetails.getUsername()).orElseThrow());
        return "profile";
    }

    @PostMapping("/change-password")
    public String changePassword(@AuthenticationPrincipal UserDetails userDetails,
                                 @RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 RedirectAttributes ra) {
        if (!newPassword.equals(confirmPassword)) {
            ra.addFlashAttribute("error", "New passwords do not match");
            return "redirect:/profile";
        }
        if (!passwordValidator.isValid(newPassword)) {
            ra.addFlashAttribute("error", passwordValidator.getRequirements());
            return "redirect:/profile";
        }
        try {
            var user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
            userService.changePassword(user.getId(), currentPassword, newPassword);
            ra.addFlashAttribute("success", "Password changed successfully");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/profile";
    }
}
