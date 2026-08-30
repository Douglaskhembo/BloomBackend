package com.bloom.bloomschool.gradeLevel.dto;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GradeEntryDto {
    private String label;
    private Double minScore;
    private Double maxScore;
    private int points;
    private String remarks;
}
