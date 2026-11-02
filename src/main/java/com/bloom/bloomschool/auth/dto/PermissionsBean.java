package com.bloom.bloomschool.auth.dto;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionsBean {
    private String name;
    private String permissionDescription;
    private String accessType;
    private UUID moduleUuid;
    private UUID roleUuid;
    private UUID permissionUuid;
    private UUID userUuid;
    private List<UUID> permissionUuids;
    private String overrideType;

}
