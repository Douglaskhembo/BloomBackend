package com.bloom.bloomschool.payments.security;

import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HexFormat;

/**
 * HMAC-SHA256 webhook signature verification, shared across bank integrations that sign
 * their payload this way (the common scheme across Equity Jenga / KCB Buni / Co-op-style
 * partner APIs). Accepts either hex or base64 encoded signatures since banks differ on
 * which they use — confirm the exact encoding against each bank's docs before production use.
 */
@Component
public class HmacSignatureVerifier {

    public boolean verify(String rawBody, String providedSignature, String secret) {
        if (providedSignature == null || providedSignature.isBlank() || secret == null || secret.isBlank()) {
            return false;
        }
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] computed = mac.doFinal(rawBody.getBytes(StandardCharsets.UTF_8));

            String computedHex = HexFormat.of().formatHex(computed);
            String computedBase64 = Base64.getEncoder().encodeToString(computed);

            return constantTimeEquals(providedSignature, computedHex) || constantTimeEquals(providedSignature, computedBase64);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean constantTimeEquals(String a, String b) {
        return MessageDigest.isEqual(a.getBytes(StandardCharsets.UTF_8), b.getBytes(StandardCharsets.UTF_8));
    }
}
