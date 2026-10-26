package com.bloom.bloomschool.paymentTest.service;


import com.bloom.bloomschool.paymentTest.config.MpesaProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MpesaClient {
    private final MpesaProperties mpesaProperties;
    private final RestTemplate restTemplate = new RestTemplate();

    private String cachedToken = null;
    private long tokenExpireTimeMilli = 0;

    public synchronized String getAccessToken(){
        if (cachedToken != null && System.currentTimeMillis()<tokenExpireTimeMilli){
            return cachedToken;
        }

        String authString = mpesaProperties.getConsumerKey()+":"+mpesaProperties.getConsumerSecret();
        String base64Auth = Base64.getEncoder().encodeToString(authString.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + base64Auth);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        String url = mpesaProperties.getBaseUrl()+"/oauth/v1/generate?grant_type=client_credentials";
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);

        if(response.getBody() != null){
            cachedToken = (String) response.getBody().get("access_token");

            long expiresInSeconds = Long.parseLong((String) response.getBody().get("expires_in"));
            tokenExpireTimeMilli = System.currentTimeMillis()+((expiresInSeconds - 60)*1000);
            return cachedToken;
        }

        throw new RuntimeException("Could not fetch access token");

    }

    public Map<String, Object> initiateStkPush(String phoneNumber, String amount, String accountReference){
        String accessToken = getAccessToken();

        String timeStamp = LocalDateTime.now(ZoneId.of("Africa/Nairobi"))
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        String password = mpesaProperties.getShortcode()+mpesaProperties.getPasskey()+timeStamp;
        String base64Password = Base64.getEncoder().encodeToString(password.getBytes());

        Map<String, Object> payload = new HashMap<>();
        payload.put("BusinessShortCode", mpesaProperties.getShortcode());
        payload.put("Password", base64Password);
        payload.put("Timestamp", timeStamp);
        payload.put("TransactionType", "CustomerPayBillOnline");
        payload.put("Amount", amount);
        payload.put("PartyA", phoneNumber);
        payload.put("PartyB", mpesaProperties.getShortcode());
        payload.put("PhoneNumber", phoneNumber);
        payload.put("CallBackURL", mpesaProperties.getCallbackUrl());
        payload.put("AccountReference", accountReference);
        payload.put("TransactionDesc", "Payment for" + accountReference);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer "+accessToken);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        String url = mpesaProperties.getBaseUrl()+"/mpesa/stkpush/v1/processrequest";
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);

        return response.getBody();
    }

    
}
