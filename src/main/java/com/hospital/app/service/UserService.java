package com.hospital.app.service;

import com.hospital.app.entity.Role;
import com.hospital.app.entity.User;
import com.hospital.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private static final int MAX_FAILED_ATTEMPTS = 5;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    @Transactional
    public User createUser(String username, String rawPassword, String email, Role role) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already taken: " + username);
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered: " + email);
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setEmail(email);
        user.setRole(role);
        User saved = userRepository.save(user);
        log.info("AUDIT: New user created - username={}, role={}", username, role);
        return saved;
    }

    @Transactional
    public void adminResetPassword(Long userId, String newRaw) {
        User user = findById(userId);
        user.setPassword(passwordEncoder.encode(newRaw));
        user.setLastPasswordChange(LocalDateTime.now());
        user.setFailedLoginAttempts(0);
        user.setAccountNonLocked(true);
        userRepository.save(user);
        log.info("AUDIT: Password reset by admin for userId={}", userId);
    }

    @Transactional
    public void changePassword(Long userId, String currentRaw, String newRaw) {
        User user = findById(userId);
        if (!passwordEncoder.matches(currentRaw, user.getPassword())) {
            log.warn("AUDIT: Failed password change attempt for userId={}", userId);
            throw new IllegalArgumentException("Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(newRaw));
        user.setLastPasswordChange(LocalDateTime.now());
        userRepository.save(user);
        log.info("AUDIT: Password changed for userId={}", userId);
    }

    @Transactional
    public void recordFailedLogin(String username) {
        userRepository.findByUsername(username).ifPresent(user -> {
            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
            if (user.getFailedLoginAttempts() >= MAX_FAILED_ATTEMPTS) {
                user.setAccountNonLocked(false);
                log.warn("AUDIT: Account locked after {} failed attempts - username={}", MAX_FAILED_ATTEMPTS, username);
            }
            userRepository.save(user);
        });
    }

    @Transactional
    public void resetFailedAttempts(String username) {
        userRepository.findByUsername(username).ifPresent(user -> {
            user.setFailedLoginAttempts(0);
            userRepository.save(user);
        });
    }

    @Transactional
    public void deleteUser(Long id) {
        log.info("AUDIT: User deleted - id={}", id);
        userRepository.deleteById(id);
    }
}
