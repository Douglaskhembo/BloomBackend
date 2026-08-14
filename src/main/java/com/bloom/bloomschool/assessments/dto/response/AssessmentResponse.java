package com.bloom.bloomschool.assessments.dto.response;

import com.bloom.bloomschool.assessments.entity.Assessment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentResponse {
    private UUID uuid;
    private String name;
    private Assessment.Type type;
    private String term;
    private int year;
    private UUID subjectUuid;
    private String subjectName;
    private UUID gradeLevelUuid;
    private String grade;
    private String stream;
    private double maxScore;
    private long studentCount;
    private long gradedCount;
}
