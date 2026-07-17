package com.bloom.bloomschool.subjects.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class SubjectRequest {
    @NotBlank private String name;
    private Long id;
    private UUID uuid;
    private String code;
    private String grade;
    private String description;
}
