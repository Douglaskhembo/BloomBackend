package com.bloom.bloomschool.staff.entity;

import com.bloom.bloomschool.common.entity.BaseEntity;
import com.bloom.bloomschool.payroll.entity.Bank;
import com.bloom.bloomschool.payroll.entity.MobileMoneyProvider;
import com.bloom.bloomschool.payroll.entity.PaymentType;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Finance-owned staff payout details (bank account / mobile money) used to prepare payroll for
 * disbursement. Deliberately kept separate from onboarding/Staff — only Finance captures this.
 */
@Entity
@Table(name = "bloom_sch_staff_payment_details")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StaffPaymentDetails extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @PrePersist
    public void generateUuid() { if (uuid == null) uuid = UUID.randomUUID(); }

    /** Matches Staff.uuid.toString() — same loosely-coupled convention as StaffSalary.staffId. */
    @Column(unique = true, nullable = false)
    private String staffId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    /** EAGER (like {@code PayrollMaker.user}) — this entity is serialized directly to JSON, and a
     *  lazy proxy here fails Jackson serialization instead of triggering initialization. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bank_id")
    private Bank bank;

    private String bankAccountNumber;
    private String bankAccountName;
    private String bankBranch;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "mobile_money_provider_id")
    private MobileMoneyProvider mobileMoneyProvider;

    private String mobileNumber;
    private String mobileAccountName;

    /** Settlement rail for the bank-submission export (e.g. PESALINK/RTGS/EFT/WITHIN BANK for bank
     *  transfers, MPESA/AIRTEL MONEY for mobile wallets). EAGER for the same reason as bank/provider above. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "payment_type_id")
    private PaymentType paymentType;

    public enum PaymentMethod { BANK_TRANSFER, MOBILE_MONEY, CHEQUE }
}
