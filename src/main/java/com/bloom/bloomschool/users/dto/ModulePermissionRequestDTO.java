package com.bloom.bloomschool.users.dto;

import lombok.*;

import java.util.Set;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ModulePermissionRequestDTO {
    private UUID moduleUuid;
    private Set<UUID> permissionUuids;
}
