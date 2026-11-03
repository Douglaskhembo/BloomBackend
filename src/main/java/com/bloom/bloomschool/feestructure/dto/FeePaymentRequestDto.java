package com.bloom.bloomschool.feestructure.dto;

import com.bloom.bloomschool.feestructure.enumerations.FeePaymentMethod;
import com.bloom.bloomschool.feestructure.enumerations.FeePaymentSource;
import com.bloom.bloomschool.feestructure.enumerations.FeeVerificationStatus;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class FeePaymentRequestDto {
    private UUID studentUuid;
    private UUID admissionUuid;
    private String grade;
    private String stream;
    private String account;
    private Double expectedAmt;
    private FeePaymentMethod paymentMethod;
    private String reference;
    private LocalDateTime paymentDate;
    private FeePaymentSource paymentSource;
    private FeeVerificationStatus verificationStatus;
    private String rejectReason;
    private String chequeNo;
    private String notes;
    private String bankName;
    private Double amount;
}