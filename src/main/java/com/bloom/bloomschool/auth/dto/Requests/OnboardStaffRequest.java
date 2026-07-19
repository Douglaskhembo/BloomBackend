package com.bloom.bloomschool.auth.dto.Requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class OnboardStaffRequest {
    @NotNull private UUID staffUuid;
    @NotBlank private String userName;
    private Set<UUID> roleUuids;
    private boolean enable2FA;
}
