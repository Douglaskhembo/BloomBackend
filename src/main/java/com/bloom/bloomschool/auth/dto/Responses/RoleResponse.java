package com.bloom.bloomschool.auth.dto.Responses;

import lombok.Builder;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class RoleResponse {
    private UUID roleUuid;
    private String roleName;
    private Set<String> permissions;
}
