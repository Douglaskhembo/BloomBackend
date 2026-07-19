package com.bloom.bloomschool.payments.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class StkPushRequest {
    /** Student admission number — sent as the M-Pesa AccountReference. */
    @NotBlank
    private String admissionNumber;

    /** Payer's phone, e.g. 07XXXXXXXX or 2547XXXXXXXX. If omitted, falls back to the student's parentPhone. */
    private String phone;

    @NotNull
    @Positive
    private Double amount;
}
