package com.bloom.bloomschool.staff.dto;

import lombok.*;

import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class StaffRoleResponseDto {
    private UUID uuid;

    private String name;

    private String description;

    private boolean status;
}
