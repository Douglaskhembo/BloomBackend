package com.bloom.bloomschool.common.crypto;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * AES-256-GCM encryption for sensitive byte-array columns (currently: fingerprint templates).
 * A random 12-byte IV is generated per value and prepended to the ciphertext+auth-tag, so no
 * separate IV column is needed. Key comes from app.biometrics.encryption-key — override via the
 * BIOMETRIC_ENCRYPTION_KEY env var in any real deployment; the checked-in default is dev-only.
 */
@Converter
@Component
public class AesGcmByteConverter implements AttributeConverter<byte[], byte[]> {

    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int IV_LENGTH_BYTES = 12;
    private static final int TAG_LENGTH_BITS = 128;

    private final SecretKeySpec key;
    private final SecureRandom random = new SecureRandom();

    public AesGcmByteConverter(@Value("${app.biometrics.encryption-key}") String base64Key) {
        this.key = new SecretKeySpec(Base64.getDecoder().decode(base64Key), "AES");
    }

    @Override
    public byte[] convertToDatabaseColumn(byte[] plain) {
        if (plain == null) return null;
        try {
            byte[] iv = new byte[IV_LENGTH_BYTES];
            random.nextBytes(iv);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH_BITS, iv));
            byte[] ciphertext = cipher.doFinal(plain);

            byte[] result = new byte[IV_LENGTH_BYTES + ciphertext.length];
            System.arraycopy(iv, 0, result, 0, IV_LENGTH_BYTES);
            System.arraycopy(ciphertext, 0, result, IV_LENGTH_BYTES, ciphertext.length);
            return result;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to encrypt column value", e);
        }
    }

    @Override
    public byte[] convertToEntityAttribute(byte[] stored) {
        if (stored == null) return null;
        try {
            byte[] iv = new byte[IV_LENGTH_BYTES];
            System.arraycopy(stored, 0, iv, 0, IV_LENGTH_BYTES);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH_BITS, iv));
            return cipher.doFinal(stored, IV_LENGTH_BYTES, stored.length - IV_LENGTH_BYTES);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to decrypt column value", e);
        }
    }
}
