package com.bloom.bloomschool.payroll.dto;

import com.bloom.bloomschool.payroll.entity.AllowanceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class AllowanceTypeRequest {
    @NotBlank private String name;
    @NotNull private AllowanceType.ValueType type;
    @NotNull private Double defaultValue;
    private boolean taxable;
    private Long id;
    private UUID uuid;
}
