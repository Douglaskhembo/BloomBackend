package com.bloom.bloomschool.gradeLevel.dto;

import lombok.*;

import java.util.List;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class GradeStructureRequestDto {
    private String grade;
    private List<GradeEntryRequestDto> gradingEntries;
}
