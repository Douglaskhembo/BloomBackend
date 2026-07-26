package com.bloom.bloomschool.payroll.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MobileMoneyProviderRequest {
    @NotBlank private String name;
    private String shortCode;
}
