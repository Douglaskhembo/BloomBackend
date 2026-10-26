package com.bloom.bloomschool.feestructure.entity;

import com.bloom.bloomschool.feestructure.enumerations.FeePaymentMethod;
import com.bloom.bloomschool.feestructure.enumerations.FeePaymentSource;
import com.bloom.bloomschool.feestructure.enumerations.FeeVerificationStatus;
import com.bloom.bloomschool.student.entity.Admissions;
import com.bloom.bloomschool.student.entity.Student;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@Table(name = "bloom_school_fee_payment")
public class FeePayment{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    private String grade;

    private String stream;

    private String account;

    private Double expectedAmt;

    @Enumerated(EnumType.STRING)
    private FeePaymentMethod paymentMethod;

    private String reference;

    private LocalDateTime paymentDate;

    @Enumerated(EnumType.STRING)
    private FeePaymentSource paymentSource;

    @Enumerated(EnumType.STRING)
    private FeeVerificationStatus verificationStatus;

    private String rejectReason;

    private String chequeNo;

    @Column(unique = true)
    private String receiptNo;

    private String notes;

    private String bankName;

    private double amount;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "admission_id")
    private Admissions admissions;

}
