package com.bloom.bloomschool.subject.dto;

import com.bloom.bloomschool.gradeLevel.dto.GradeResponseDto;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class SubjectResponseDto {
    private UUID uuid;
    private String name;
    private String subjectCode;
    private Boolean isActive;
    private List<GradeResponseDto> grades;
}
