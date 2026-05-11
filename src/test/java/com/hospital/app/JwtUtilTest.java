package com.hospital.app;

import com.hospital.app.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void generatedTokenIsValid() {
        String token = jwtUtil.generateToken("admin", "ADMIN");
        assertTrue(jwtUtil.validateToken(token));
    }

    @Test
    void usernameIsExtractedCorrectly() {
        String token = jwtUtil.generateToken("dr.smith", "DOCTOR");
        assertEquals("dr.smith", jwtUtil.getUsernameFromToken(token));
    }

    @Test
    void roleIsExtractedCorrectly() {
        String token = jwtUtil.generateToken("admin", "ADMIN");
        assertEquals("ADMIN", jwtUtil.getRoleFromToken(token));
    }

    @Test
    void tamperedTokenIsInvalid() {
        String token = jwtUtil.generateToken("admin", "ADMIN");
        String tampered = token.substring(0, token.length() - 5) + "XXXXX";
        assertFalse(jwtUtil.validateToken(tampered));
    }

    @Test
    void emptyTokenIsInvalid() {
        assertFalse(jwtUtil.validateToken(""));
    }

    @Test
    void randomStringIsInvalidToken() {
        assertFalse(jwtUtil.validateToken("not.a.jwt.token"));
    }
}
