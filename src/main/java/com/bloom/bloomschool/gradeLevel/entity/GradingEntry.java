package com.bloom.bloomschool.gradeLevel.entity;


import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GradingEntry {
    private String label;
    private Double minScore;
    private Double maxScore;
    private int points;
    private String remarks;
}


