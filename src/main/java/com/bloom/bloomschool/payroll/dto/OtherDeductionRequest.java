package com.bloom.bloomschool.payroll.dto;

import com.bloom.bloomschool.payroll.entity.OtherDeduction;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class OtherDeductionRequest {
    @NotBlank private String name;
    @NotNull private OtherDeduction.ValueType type;
    @NotNull private Double defaultValue;
    private boolean mandatory;
    private Long id;
    private UUID uuid;
}
