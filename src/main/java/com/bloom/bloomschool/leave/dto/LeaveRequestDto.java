package com.bloom.bloomschool.leave.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class LeaveRequestDto {
    @NotBlank private String staffId;
    @NotBlank private String staffName;
    @NotNull private Long leaveTypeId;
    @NotNull private LocalDate fromDate;
    @NotNull private LocalDate toDate;
    private String reason;
    private Long id;
    private UUID uuid;
}
