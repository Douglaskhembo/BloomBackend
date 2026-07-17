package com.bloom.bloomschool.auth.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class UserRolesDTO {
    private UUID userUuid;
    private List<UUID> roleUuids;
}
