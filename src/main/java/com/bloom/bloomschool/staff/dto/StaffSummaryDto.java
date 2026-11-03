package com.bloom.bloomschool.staff.dto;

import lombok.*;

import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class StaffSummaryDto {
    private UUID uuid;
    private String fullName;
    private String email;
}
