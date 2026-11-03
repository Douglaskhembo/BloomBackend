package com.bloom.bloomschool.assessment.dto;

import com.bloom.bloomschool.assessment.AssessmentType;
import com.bloom.bloomschool.gradeLevel.dto.GradeSummaryDto;
import com.bloom.bloomschool.staff.dto.StaffSummaryDto;
import com.bloom.bloomschool.student.dto.SubjectSummaryDto;
import lombok.*;

import java.util.UUID;
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class AssessmentResponseDto {
    private UUID uuid;
    private String name;
    private String term;
    private int maxScore;
    private int year;
    private String stream;
    private AssessmentType assessmentType;
    private GradeSummaryDto grade;
    private SubjectSummaryDto subject;
    private StaffSummaryDto staff;
}
