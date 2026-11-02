package com.bloom.bloomschool.users.dto;

import lombok.*;

import java.util.Set;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRolesRequestDTO {
    private UUID userUuid;
    private Set<UUID> roleUuids;

}
