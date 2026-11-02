package com.bloom.bloomschool.users.dto;

import lombok.*;

import java.util.Set;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ModuleRoleRequestDTO {
    private UUID roleUuid;
    private Set<UUID> moduleUuids;
}
