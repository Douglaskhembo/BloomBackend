package com.bloom.bloomschool.assessment.dto;

import com.bloom.bloomschool.assessment.AssessmentType;
import com.bloom.bloomschool.gradeLevel.dto.GradeDto;
import com.bloom.bloomschool.staff.dto.StaffDto;
import com.bloom.bloomschool.subject.dto.SubjectDto;
import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentDto {
    private String name;
    private String term;
    private int maxScore;
    private int year;
    private String stream;
    private AssessmentType assessmentType;
    private GradeDto grade;
    private SubjectDto subject;
    private StaffDto staff;
    private UUID gradeUuid;
    private UUID subjectUuid;
    private UUID staffUuid;
}
