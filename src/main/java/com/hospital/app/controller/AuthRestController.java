package com.hospital.app.controller;

import com.hospital.app.dto.JwtRequest;
import com.hospital.app.entity.User;
import com.hospital.app.repository.UserRepository;
import com.hospital.app.security.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthRestController {

    private static final Logger log = LoggerFactory.getLogger(AuthRestController.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/token")
    public ResponseEntity<?> getToken(@Valid @RequestBody JwtRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElse(null);

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("AUDIT: Failed JWT token request for username={}", request.getUsername());
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }

        if (!user.isAccountNonLocked() || !user.isEnabled()) {
            return ResponseEntity.status(403).body(Map.of("error", "Account is locked or disabled"));
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        log.info("AUDIT: JWT token issued for username={}", user.getUsername());
        return ResponseEntity.ok(Map.of("token", token, "role", user.getRole().name()));
    }
}
