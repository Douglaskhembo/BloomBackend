package com.bloom.bloomschool.auth.dto.Responses;

import lombok.Builder;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class UserResponse {
    private UUID userUuid;
    private String userName;
    private String firstName;
    private String otherNames;
    private String email;
    private String phoneNumber;
    private boolean active;
    private boolean firstLogin;
    private boolean enable2FA;
    private boolean accountLocked;
    private Integer failedLoginAttempts = 0;
    private String profileRef;
    private Set<String> roles;
    private String roleName;   // primary role name (first)
    private UUID roleUuid;     // primary role uuid (first)
}
