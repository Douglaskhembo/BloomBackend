package com.bloom.bloomschool.payments.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Normalized shape every bank-specific webhook payload gets mapped into before
 * hitting the shared reconciliation logic — keeps PaymentReconciliationService
 * bank-agnostic.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankWebhookRequest {
    /** Account number the depositor entered — expected to be the student admission number. */
    private String accountNumber;
    private Double amount;
    private String transactionRef;
    private String payerName;
    private String narrative;
}
