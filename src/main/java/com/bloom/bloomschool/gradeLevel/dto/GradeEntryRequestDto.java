package com.bloom.bloomschool.gradeLevel.dto;

import lombok.*;


@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class GradeEntryRequestDto {
     private String label;
     private Double minScore;
     private Double maxScore;
     private Integer points;
     private String remarks;
}