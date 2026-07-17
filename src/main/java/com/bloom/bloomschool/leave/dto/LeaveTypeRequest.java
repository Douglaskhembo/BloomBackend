package com.bloom.bloomschool.leave.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class LeaveTypeRequest {
    @NotBlank private String name;
    @NotNull private Integer maxDaysPerYear;
    private boolean requiresApproval = true;
    private Long id;
    private UUID uuid;
}
