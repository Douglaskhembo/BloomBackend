package com.bloom.bloomschool.staff.dto;

import lombok.*;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class StaffRoleRequestDto {
    private String name;

    private String description;

    private boolean status;
}
