package com.bloom.bloomschool.payments.client;

import com.bloom.bloomschool.payments.config.MpesaProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Thin client over the Daraja OAuth, STK Push and C2B URL-registration endpoints.
 * Uses RestTemplate (blocking) since this project has no WebFlux dependency — fine
 * for the low, bursty call volume these endpoints see.
 */
@Component
@RequiredArgsConstructor
public class MpesaClient {

    private static final Logger log = LoggerFactory.getLogger(MpesaClient.class);

    private final RestTemplate restTemplate;
    private final MpesaProperties props;

    private String accessToken;
    private long tokenExpiryEpochMs;

    @PostConstruct
    public void init() {
        if (isBlank(props.getConsumerKey()) || isBlank(props.getConsumerSecret()) || isBlank(props.getShortcode())) {
            log.warn("M-Pesa is not configured (missing consumerKey/consumerSecret/shortcode) — payment endpoints will fail until mpesa.* properties are set.");
            return;
        }
        try {
            registerC2BUrls();
        } catch (Exception e) {
            log.warn("C2B URL registration skipped at startup: {}", e.getMessage());
        }
    }

    public synchronized String getAccessToken() {
        if (accessToken == null || Instant.now().toEpochMilli() >= tokenExpiryEpochMs) {
            refreshAccessToken();
        }
        return accessToken;
    }

    @SuppressWarnings("unchecked")
    private synchronized void refreshAccessToken() {
        String auth = Base64.getEncoder().encodeToString(
                (props.getConsumerKey() + ":" + props.getConsumerSecret()).getBytes(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + auth);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        Map<String, Object> body = restTemplate.exchange(
                props.getBaseUrl() + props.getOauthPath(), HttpMethod.GET, entity, Map.class).getBody();

        if (body == null || !body.containsKey("access_token")) {
            throw new IllegalStateException("Failed to retrieve M-Pesa access token");
        }
        accessToken = (String) body.get("access_token");
        int expiresIn = Integer.parseInt(body.get("expires_in").toString());
        tokenExpiryEpochMs = Instant.now().toEpochMilli() + (expiresIn - 60) * 1000L;
    }

    public String generatePassword(String timestamp) {
        String data = props.getEffectiveStkShortcode() + props.getPasskey() + timestamp;
        return Base64.getEncoder().encodeToString(data.getBytes(StandardCharsets.UTF_8));
    }

    /** Sends the STK push and returns Safaricom's immediate ack (contains CheckoutRequestID/MerchantRequestID). */
    @SuppressWarnings("unchecked")
    public Map<String, Object> stkPush(String phone, double amount, String accountReference) {
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        Map<String, Object> payload = new HashMap<>();
        payload.put("BusinessShortCode", props.getEffectiveStkShortcode());
        payload.put("Password", generatePassword(timestamp));
        payload.put("Timestamp", timestamp);
        payload.put("TransactionType", "CustomerPayBillOnline");
        payload.put("Amount", (long) Math.ceil(amount));
        payload.put("PartyA", phone);
        payload.put("PartyB", props.getEffectiveStkShortcode());
        payload.put("PhoneNumber", phone);
        payload.put("CallBackURL", props.getCallbackBaseUrl() + props.getStkCallbackPath());
        payload.put("AccountReference", accountReference);
        payload.put("TransactionDesc", "School fees payment");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + getAccessToken());

        return restTemplate.exchange(
                props.getBaseUrl() + props.getStkPushPath(), HttpMethod.POST,
                new HttpEntity<>(payload, headers), Map.class).getBody();
    }

    public void registerC2BUrls() {
        Map<String, Object> body = new HashMap<>();
        body.put("ShortCode", props.getShortcode());
        body.put("ResponseType", "Completed");
        body.put("ConfirmationURL", props.getCallbackBaseUrl() + props.getConfirmationPath());
        body.put("ValidationURL", props.getCallbackBaseUrl() + props.getValidationPath());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + getAccessToken());

        String resp = restTemplate.postForObject(
                props.getBaseUrl() + props.getC2bRegisterPath(),
                new HttpEntity<>(body, headers), String.class);
        log.info("M-Pesa C2B URL registration response: {}", resp);
    }

    private static boolean isBlank(String s) { return s == null || s.isBlank(); }
}
