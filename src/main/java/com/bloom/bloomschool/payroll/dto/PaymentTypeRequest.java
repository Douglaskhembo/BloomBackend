package com.bloom.bloomschool.payroll.dto;

import com.bloom.bloomschool.payroll.entity.PaymentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentTypeRequest {
    @NotNull private PaymentType.Category category;
    @NotBlank private String code;
}
