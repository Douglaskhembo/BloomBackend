package com.bloom.bloomschool.gradeLevel.dto;

import lombok.*;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class GradeEntryResponseDto {
    private String label; private Double minScore; private Double maxScore; private int points; private String remarks;
}