package com.hospital.app;

import com.hospital.app.security.PasswordValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PasswordValidatorTest {

    @Autowired
    private PasswordValidator passwordValidator;

    @Test
    void strongPasswordIsValid() {
        assertTrue(passwordValidator.isValid("Admin@1234!"));
    }

    @Test
    void passwordWithoutUppercaseIsInvalid() {
        assertFalse(passwordValidator.isValid("admin@1234!"));
    }

    @Test
    void passwordWithoutLowercaseIsInvalid() {
        assertFalse(passwordValidator.isValid("ADMIN@1234!"));
    }

    @Test
    void passwordWithoutDigitIsInvalid() {
        assertFalse(passwordValidator.isValid("Admin@abcd!"));
    }

    @Test
    void passwordWithoutSpecialCharIsInvalid() {
        assertFalse(passwordValidator.isValid("Admin12345"));
    }

    @Test
    void shortPasswordIsInvalid() {
        assertFalse(passwordValidator.isValid("Ad@1"));
    }

    @Test
    void nullPasswordIsInvalid() {
        assertFalse(passwordValidator.isValid(null));
    }
}
