package com.bloom.bloomschool.school.dto;

import lombok.*;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class DepartmentResponseDTO {
    private UUID uuid;
    private String departmentName;
    private String description;
    private String departmentCode;

}
