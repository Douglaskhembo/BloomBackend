package com.bloom.bloomschool.assessment.dto;

import com.bloom.bloomschool.assessment.AssessmentType;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentRequestDto {
     private String name;
     private String term;
     private Integer maxScore;
     private Integer year;
     private String stream;
     private AssessmentType assessmentType;
     private UUID gradeUuid;
     private UUID subjectUuid;
     private UUID staffUuid;
}