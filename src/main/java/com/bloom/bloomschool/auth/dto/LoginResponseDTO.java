package com.bloom.bloomschool.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    private String accessToken;
    private String refreshToken;
    private UUID uuid;
    private String username;
    private String fullName;
    private String otherNames;
    private String role;
    private Boolean twoFactorRequired;
    private Boolean firstLogin;
}