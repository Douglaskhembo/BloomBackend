package com.bloom.bloomschool.reports.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectScoreResponse {
    private String subjectName;
    private double score;
    private String grade;
    private int points;
    private String remark;
}
