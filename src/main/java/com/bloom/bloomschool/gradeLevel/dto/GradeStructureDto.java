package com.bloom.bloomschool.gradeLevel.dto;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GradeStructureDto {
    private UUID uuid;
    private String grade;
    private List<GradeEntryDto> gradingEntries;
}
