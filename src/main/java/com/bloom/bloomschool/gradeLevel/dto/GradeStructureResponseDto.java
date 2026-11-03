package com.bloom.bloomschool.gradeLevel.dto;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class GradeStructureResponseDto {
    private UUID uuid;
    private String grade;
    private List<GradeEntryResponseDto> gradingEntries;
}

