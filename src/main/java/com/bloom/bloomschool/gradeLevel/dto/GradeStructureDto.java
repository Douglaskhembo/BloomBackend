package com.bloom.bloomschool.gradeLevel.dto;

import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GradeStructureDto {
    private String grade;
    private List<GradeEntryDto> gradingEntries;
}
