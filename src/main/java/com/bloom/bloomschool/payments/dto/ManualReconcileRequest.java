package com.bloom.bloomschool.payments.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ManualReconcileRequest {
    @NotBlank
    private String admissionNumber;
}
