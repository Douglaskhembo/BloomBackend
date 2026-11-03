package com.bloom.bloomschool.gradeLevel.dto;

import lombok.*;

import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class GradeSummaryDto {
    private UUID uuid;
    private String name;
}