package com.bloom.bloomschool.school.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class DepartmentRequest {
    @NotBlank private String name;
    @NotBlank private String code;
    private String head;
    private Long id;
    private UUID uuid;
}
