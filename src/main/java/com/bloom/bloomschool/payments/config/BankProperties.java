package com.bloom.bloomschool.payments.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Credentials/config for each bank webhook integration. Field names on each nested
 * config are a best-effort superset of what these APIs typically require (API key/secret,
 * merchant/account code, a signing secret for verifying inbound webhook signatures).
 * Confirm the exact auth scheme against each bank's current API documentation before
 * going live — Equity Jenga, KCB Buni and Co-op's developer portals are the source of truth,
 * not this scaffold.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "banks")
public class BankProperties {

    private BankConfig equity = new BankConfig();
    private BankConfig kcb = new BankConfig();
    private BankConfig coop = new BankConfig();

    @Data
    public static class BankConfig {
        private boolean enabled = false;
        private String baseUrl;
        private String apiKey;
        private String apiSecret;
        private String merchantCode;
        /** Secret used to verify the HMAC signature header the bank sends on each webhook call. */
        private String signatureSecret;
        /** Name of the HTTP header carrying the signature, e.g. "Signature" or "X-Jenga-Signature". */
        private String signatureHeader;
    }
}
