package com.bloom.bloomschool.payments.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Data
@Configuration
@ConfigurationProperties(prefix = "mpesa")
public class MpesaProperties {

    /** https://sandbox.safaricom.co.ke for testing, https://api.safaricom.co.ke for production. */
    private String baseUrl;
    private String oauthPath;
    private String stkPushPath;
    private String stkQueryPath;
    private String c2bRegisterPath;

    /** Paybill number used for both C2B (fee payments) and STK push. */
    private String shortcode;

    /** Only needed if STK push uses a different (e.g. till) shortcode from the C2B paybill. Defaults to shortcode. */
    private String stkShortcode;

    private String passkey;
    private String consumerKey;
    private String consumerSecret;

    /** Public base URL this backend is reachable at, used to build the callback URLs registered with Safaricom. */
    private String callbackBaseUrl;

    private String stkCallbackPath;
    private String validationPath;
    private String confirmationPath;

    /** Safaricom's published C2B/STK callback source IPs. Leave empty to disable IP filtering (e.g. sandbox testing). */
    private Set<String> whitelist = Set.of();

    public String getEffectiveStkShortcode() {
        return (stkShortcode == null || stkShortcode.isBlank()) ? shortcode : stkShortcode;
    }

    /** Filters out blank entries so an unset env var (which binds to a single "" element) reads as "no whitelist". */
    public Set<String> getEffectiveWhitelist() {
        if (whitelist == null) return Set.of();
        return whitelist.stream().filter(ip -> ip != null && !ip.isBlank()).collect(java.util.stream.Collectors.toSet());
    }
}
