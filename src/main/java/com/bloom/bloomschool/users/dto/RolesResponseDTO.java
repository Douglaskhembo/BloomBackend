package com.bloom.bloomschool.users.dto;

import lombok.*;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter

public class RolesResponseDTO {
    private UUID uuid;
    private String name;
}
