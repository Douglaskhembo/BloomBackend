package com.bloom.bloomschool.assessment.dto;


import com.bloom.bloomschool.student.dto.StudentSummaryDto;
import lombok.*;

import java.util.UUID;
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class AssessmentMarkResponseDto {
    private UUID uuid;
    private Double score;
    private UUID assessmentUuid;
    private String assessmentName;
    private StudentSummaryDto student;
}