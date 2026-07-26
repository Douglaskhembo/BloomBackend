package com.bloom.bloomschool.payroll.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BankRequest {
    @NotBlank private String name;
    private String bankCode;
    private String swiftCode;
}
