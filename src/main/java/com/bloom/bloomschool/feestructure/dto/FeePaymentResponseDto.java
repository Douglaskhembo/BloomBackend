package com.bloom.bloomschool.feestructure.dto;

import com.bloom.bloomschool.feestructure.enumerations.FeePaymentMethod;
import com.bloom.bloomschool.feestructure.enumerations.FeePaymentSource;
import com.bloom.bloomschool.feestructure.enumerations.FeeVerificationStatus;
import com.bloom.bloomschool.student.dto.StudentSummaryDto;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class FeePaymentResponseDto {
    private UUID uuid;
    private String grade;
    private String stream;
    private String account;
    private Double expectedAmt;
    private FeePaymentMethod paymentMethod;
    private String reference;
    private String receiptNo;
    private LocalDateTime paymentDate;
    private FeePaymentSource paymentSource;
    private FeeVerificationStatus verificationStatus;
    private String rejectReason;
    private String chequeNo;
    private String notes;
    private String bankName;
    private double amount;
    private StudentSummaryDto student;
    private UUID admissionUuid;
}
