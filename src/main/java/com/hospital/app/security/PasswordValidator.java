package com.hospital.app.security;

import org.springframework.stereotype.Component;

@Component
public class PasswordValidator {

    private static final String PASSWORD_PATTERN =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&_#\\-])[A-Za-z\\d@$!%*?&_#\\-]{8,}$";

    public boolean isValid(String password) {
        return password != null && password.matches(PASSWORD_PATTERN);
    }

    public String getRequirements() {
        return "Password must be at least 8 characters and include uppercase, lowercase, digit, and special character (@$!%*?&_#-)";
    }
}
