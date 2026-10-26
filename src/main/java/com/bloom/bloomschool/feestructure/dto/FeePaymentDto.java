package com.bloom.bloomschool.feestructure.dto;

import com.bloom.bloomschool.feestructure.enumerations.FeePaymentMethod;
import com.bloom.bloomschool.feestructure.enumerations.FeePaymentSource;
import com.bloom.bloomschool.feestructure.enumerations.FeeVerificationStatus;
import com.bloom.bloomschool.student.dto.StudentRequestDTO;
import com.bloom.bloomschool.student.entity.Student;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class FeePaymentDto {
    private UUID uuid;
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
    private String receiptNo;
    private String notes;
    private String bankName;
    private double amount;
    private StudentRequestDTO studentRequestDTO;
    private UUID studentUuid;
    private String studentName;
}
