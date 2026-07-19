package com.bloom.bloomschool.payroll.dto;

import com.bloom.bloomschool.payroll.entity.StatutoryDeduction;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class StatutoryDeductionRequest {
    @NotBlank private String name;
    @NotNull private StatutoryDeduction.ValueType type;
    @NotNull private StatutoryDeduction.Category category;
    @NotNull private Double value;
    private Double maxAmount;
    private boolean employerContribution;
    private double employerValue;
    private Long id;
    private UUID uuid;
}
