package com.bloom.bloomschool.auth.dto.Responses;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class PermissionResponse {
    private UUID permUuid;
    private String name;
    private String permDesc;
    private String accessType;
    private boolean granted;
}
