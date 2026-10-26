package com.bloom.bloomschool.users.dto;


import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private UUID uuid;

    private String username;

    private String otherNames;

    private String email;

    private String firstName;

    private String phoneNumber;

    private String sessionToken;

    @Builder.Default
    private Boolean active = false;

    @Builder.Default
    private Boolean firstLogin = true;

    @Builder.Default
    private Boolean accountLocked = false;

    private Integer failedLoginAttempts;

    private Boolean enable2FA;

    private LocalDateTime passwordExpiry;

    private Set<RolesResponseDTO> roles;

}
