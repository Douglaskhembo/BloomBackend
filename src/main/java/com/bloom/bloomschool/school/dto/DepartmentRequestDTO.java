package com.bloom.bloomschool.school.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class DepartmentRequestDTO {
    private String departmentName;
    private String description;
    private String departmentCode;
}
