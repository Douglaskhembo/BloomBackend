package com.bloom.bloomschool.paymentTest.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "daraja")
@Setter
@Getter
public class MpesaProperties {
    private String shortcode;
    private String consumerKey;
    private String consumerSecret;
    private String passkey;
    private String baseUrl;
    private String callbackUrl;
}


