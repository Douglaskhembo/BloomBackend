package com.bloom.bloomschool.fees.dto;

import com.bloom.bloomschool.fees.entity.FeePayment;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class FeePaymentRequest {
    // Exactly one of studentId (post-enrollment) / admissionUuid (pre-enrollment) is required —
    // enforced in FeeService.recordPayment, not here, since it's a cross-field rule.
    private String studentId;
    private UUID admissionUuid;
    private String studentName;
    private String grade;
    private String stream;
    @NotNull private Double amount;
    private Double expectedAmount;
    @NotNull private FeePayment.PaymentMethod method;
    // Required for all methods except CASH — cash payments get a system-generated reference
    // (see FeeService.recordPayment / RefGeneratorService), so the UI never collects one for cash.
    private String reference;
    private LocalDateTime paymentDate;
    private Long id;
    private UUID uuid;

    // Manual-capture only (cash/cheque/bank-slip walk-in payments) — ignored for gateway-sourced payments
    private String bankName;
    private String slipOrChequeNumber;
    private String notes;
}
