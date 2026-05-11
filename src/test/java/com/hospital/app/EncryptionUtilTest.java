package com.hospital.app;

import com.hospital.app.util.EncryptionUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EncryptionUtilTest {

    @Autowired
    private EncryptionUtil encryptionUtil;

    @Test
    void encryptedTextDiffersFromPlaintext() {
        String plain = "Patient has hypertension";
        String encrypted = encryptionUtil.encrypt(plain);
        assertNotEquals(plain, encrypted);
    }

    @Test
    void decryptionRestoresOriginalText() {
        String plain = "Diagnosis: Type 2 Diabetes";
        String encrypted = encryptionUtil.encrypt(plain);
        String decrypted = encryptionUtil.decrypt(encrypted);
        assertEquals(plain, decrypted);
    }

    @Test
    void nullInputReturnsNull() {
        assertNull(encryptionUtil.encrypt(null));
        assertNull(encryptionUtil.decrypt(null));
    }

    @Test
    void blankInputReturnsBlank() {
        assertEquals("", encryptionUtil.encrypt(""));
        assertEquals("", encryptionUtil.decrypt(""));
    }

    @Test
    void differentPlaintextsProduceDifferentCiphertexts() {
        String enc1 = encryptionUtil.encrypt("Diagnosis A");
        String enc2 = encryptionUtil.encrypt("Diagnosis B");
        assertNotEquals(enc1, enc2);
    }
}
