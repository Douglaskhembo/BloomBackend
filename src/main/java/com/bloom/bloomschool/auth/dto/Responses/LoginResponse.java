package com.bloom.bloomschool.auth.dto.Responses;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
public class LoginResponse {
    private UUID userUuid;
    private String token;
    private String username;
    private String firstName;
    private String otherNames;
    private String email;
    private String phoneNumber;
    private String role;
    private Set<String> permissions;
    private boolean firstLogin;
    private boolean requires2FA;
    private boolean enable2FA;
    private String profileRef;
    private String redirectPath;
}
