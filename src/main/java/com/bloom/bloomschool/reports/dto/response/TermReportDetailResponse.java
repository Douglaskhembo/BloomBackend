package com.bloom.bloomschool.reports.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TermReportDetailResponse {
    private UUID studentUuid;
    private String studentName;
    private String admissionNumber;
    private String grade;
    private String stream;
    private String term;
    private int year;
    private double meanScore;
    private String position;
    private List<SubjectScoreResponse> subjects;
}
