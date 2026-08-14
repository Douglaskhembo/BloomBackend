package com.bloom.bloomschool.grading.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GradingEntry {
    private String label;
    private double minScore;
    private double maxScore;
    private int points;
    private String remark;
}
