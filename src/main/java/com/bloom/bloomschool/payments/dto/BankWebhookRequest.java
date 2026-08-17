package com.bloom.bloomschool.payments.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


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
