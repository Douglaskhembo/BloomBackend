package com.bloom.bloomschool.payments.dto;

import com.bloom.bloomschool.fees.entity.FeePayment;
import com.bloom.bloomschool.payments.entity.PaymentTransaction;
import lombok.Builder;

@Builder
public record ReconcileInput(
        PaymentTransaction.Provider provider,
        String transactionRef,
        String accountReference,
        Double amount,
        String payerPhoneOrAccount,
        String payerName,
        FeePayment.PaymentMethod method,
        String rawPayload
) {}
