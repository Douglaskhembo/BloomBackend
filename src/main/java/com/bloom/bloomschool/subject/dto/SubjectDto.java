package com.bloom.bloomschool.subject.dto;

import com.bloom.bloomschool.gradeLevel.dto.GradeDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubjectDto {
    private String name;
    private String subjectCode;
    private boolean status;
    private List<UUID> gradesUuid;
    private List<GradeDto> gradeDtos;
}
