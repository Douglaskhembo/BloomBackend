package com.bloom.bloomschool.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionsBean {
    private String name;
    private String permDesc;
    private String accessType;
    private UUID moduleUuid;
    private UUID roleUuid;
    private UUID permissionUuid;
    // user-level permission overrides
    private UUID userUuid;
    private List<UUID> permissionUuids;
    private String overrideType;
}
