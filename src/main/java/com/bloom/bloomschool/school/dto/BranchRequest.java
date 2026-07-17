package com.bloom.bloomschool.school.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class BranchRequest {
    @NotBlank private String name;
    @NotBlank private String code;
    private Long id;
    private UUID uuid;
    private String location;
    private String phone;
    private Set<Long> departmentIds;
    private Set<Long> gradeLevelIds;
}
