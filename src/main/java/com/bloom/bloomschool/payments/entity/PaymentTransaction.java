package com.bloom.bloomschool.payments.entity;

import com.bloom.bloomschool.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Raw log of every inbound gateway event (M-Pesa STK/C2B, bank webhook) plus its
 * reconciliation outcome. This is the audit trail; a successfully matched transaction
 * also produces a {@link com.bloom.bloomschool.fees.entity.FeePayment} row.
 */
@Entity
@Table(name = "bloom_sch_payment_transactions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PaymentTransaction extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @PrePersist
    public void generateUuid() { if (uuid == null) uuid = UUID.randomUUID(); }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Provider provider;

    /** Gateway transaction reference: M-Pesa receipt number, or the bank's transaction ref. Unique once known. */
    @Column(unique = true)
    private String transactionRef;

    /** M-Pesa STK correlation id, set at initiation and used to match the async callback. Null for C2B/bank. */
    private String checkoutRequestId;
    private String merchantRequestId;

    /** Account/BillRef number as submitted by the payer — expected to be the student admission number. */
    private String accountReference;

    private Double amount;
    private String payerPhoneOrAccount;
    private String payerName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Status status = Status.PENDING;

    private String failureReason;

    /** Set once this transaction has been turned into a FeePayment. */
    private Long matchedFeePaymentId;

    @Column(columnDefinition = "TEXT")
    private String rawPayload;

    private LocalDateTime receivedAt;

    public enum Provider { MPESA_STK, MPESA_C2B, EQUITY, KCB, COOP }

    public enum Status {
        /** STK push sent to the payer's phone, callback not yet received. */
        PENDING,
        /** Callback/webhook received and successfully matched to a student + recorded as a FeePayment. */
        MATCHED,
        /** Callback/webhook received but no student found for the account reference — needs manual reconciliation. */
        UNMATCHED,
        /** Payment failed or was cancelled on the gateway side (e.g. STK ResultCode != 0). */
        FAILED,
    }
}
