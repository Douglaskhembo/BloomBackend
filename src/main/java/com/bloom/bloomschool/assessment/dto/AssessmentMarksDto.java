package com.bloom.bloomschool.assessment.dto;

import com.bloom.bloomschool.student.dto.StudentRequestDTO;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentMarksDto {
    private Double score;
    private AssessmentDto assessmentDtos;
    private StudentRequestDTO studentRequestDTO;
}
