package com.bloom.bloomschool.users.dto;

import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class UserPermissionResponseDTO {
    private UUID uuid;
    private String overrideType;
}
