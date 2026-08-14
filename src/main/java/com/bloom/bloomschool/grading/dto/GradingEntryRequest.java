package com.bloom.bloomschool.grading.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GradingEntryRequest {
    @NotBlank private String label;
    private double minScore;
    private double maxScore;
    private int points;
    private String remark;
}
