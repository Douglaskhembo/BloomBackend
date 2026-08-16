package com.bloom.bloomschool.assessments.dto.response;

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
public class SubjectPerformanceResponse {
    private String term;
    private int year;
    private double schoolAverage;
    private int subjectsOffered;
    private int assessmentsCount;
    private List<SubjectStat> subjects;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubjectStat {
        private UUID subjectUuid;
        private String subjectName;
        private double average;
        private double highest;
        private double lowest;
        private long markCount;
    }
}
