package com.bloom.bloomschool.fees.dto;

import com.bloom.bloomschool.fees.entity.FeePayment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class FeePaymentRequest {
    @NotBlank private String studentId;
    private String studentName;
    private String grade;
    private String stream;
    @NotNull private Double amount;
    private Double expectedAmount;
    @NotNull private FeePayment.PaymentMethod method;
    @NotBlank private String reference;
    private LocalDateTime paymentDate;
    private Long id;
    private UUID uuid;
}
