package com.bloom.bloomschool.fees.entity;

import com.bloom.bloomschool.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bloom_sch_fee_payments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FeePayment extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @PrePersist
    public void generateUuid() { if (uuid == null) uuid = UUID.randomUUID(); }

    @Column(nullable = false)
    private String studentId;       // admission number

    private String studentName;
    private String grade;
    private String stream;

    private double amount;
    private double expectedAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod method;

    @Column(unique = true, nullable = false)
    private String reference;

    /** Internal receipt number for this payment, auto-generated via RefGeneratorService — distinct from {@link #reference}, which is the external MPESA/bank/cheque reference used to dedupe gateway events. */
    @Column(unique = true)
    private String receiptNumber;

    private LocalDateTime paymentDate;

    /** Where this record came from: typed in by staff, or produced automatically off a gateway/bank event. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PaymentSource source = PaymentSource.STREAMED;

    /**
     * CASH/CARD are confirmed the instant staff capture them (money/card already settled in hand).
     * CHEQUE/BANK_TRANSFER/MPESA entered manually start PENDING_VERIFICATION until a second person
     * confirms them against the bank statement or cheque clearing (or a matching bank webhook arrives
     * and auto-confirms it, see PaymentReconciliationService) — this is what keeps a bounced cheque or
     * a slip that never clears from silently counting as paid.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private VerificationStatus verificationStatus = VerificationStatus.CONFIRMED;

    /** For manual CHEQUE/BANK_TRANSFER entries: which bank the cheque/slip is drawn on or deposited at. */
    private String bankName;

    /** Cheque number or bank deposit-slip number, as written on the physical document. */
    private String slipOrChequeNumber;

    @Column(columnDefinition = "TEXT")
    private String notes;

    /** Who confirmed (or auto-match description) / rejected a manual entry, and when. */
    private String verifiedBy;
    private LocalDateTime verifiedAt;

    @Column(columnDefinition = "TEXT")
    private String rejectionReason;

    public enum PaymentMethod { MPESA, BANK_TRANSFER, CASH, CHEQUE, CARD }

    public enum PaymentSource { MANUAL, STREAMED }

    public enum VerificationStatus { CONFIRMED, PENDING_VERIFICATION, REJECTED }
}
