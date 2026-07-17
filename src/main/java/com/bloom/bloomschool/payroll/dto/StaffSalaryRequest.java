package com.bloom.bloomschool.payroll.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class StaffSalaryRequest {
    @NotBlank private String staffId;
    @NotNull private Double basicSalary;
    private Long id;
    private UUID uuid;
    private Map<Long, Double> allowances;   // allowanceTypeId -> amount
    private Map<Long, Double> deductions;   // otherDeductionId -> amount
}
