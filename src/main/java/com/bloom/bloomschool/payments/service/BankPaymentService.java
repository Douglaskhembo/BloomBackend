package com.bloom.bloomschool.payments.service;

import com.bloom.bloomschool.fees.entity.FeePayment;
import com.bloom.bloomschool.payments.config.BankProperties;
import com.bloom.bloomschool.payments.dto.ReconcileInput;
import com.bloom.bloomschool.payments.entity.PaymentTransaction;
import com.bloom.bloomschool.payments.security.HmacSignatureVerifier;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Handles inbound bank webhook notifications (Equity, KCB, Co-op). Each bank's exact payload
 * field names and signature scheme differ and aren't fully known here — this parses the raw
 * JSON body into a Map and best-effort extracts common field-name variants, storing the full
 * raw payload on every {@link PaymentTransaction} either way so nothing is silently dropped.
 *
 * IMPORTANT: before going live with a given bank, confirm its actual webhook payload shape and
 * signature header/algorithm against that bank's current API docs/sandbox, and tighten
 * {@link #extractString} / {@link #extractAmount} candidate keys to match exactly.
 */
@Service
@RequiredArgsConstructor
public class BankPaymentService {

    private static final Logger log = LoggerFactory.getLogger(BankPaymentService.class);

    private final BankProperties bankProperties;
    private final HmacSignatureVerifier signatureVerifier;
    private final PaymentReconciliationService reconciliationService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PaymentTransaction handleEquityCallback(String rawBody, String signature) {
        return handle(PaymentTransaction.Provider.EQUITY, bankProperties.getEquity(), rawBody, signature);
    }

    public PaymentTransaction handleKcbCallback(String rawBody, String signature) {
        return handle(PaymentTransaction.Provider.KCB, bankProperties.getKcb(), rawBody, signature);
    }

    public PaymentTransaction handleCoopCallback(String rawBody, String signature) {
        return handle(PaymentTransaction.Provider.COOP, bankProperties.getCoop(), rawBody, signature);
    }

    private PaymentTransaction handle(PaymentTransaction.Provider provider, BankProperties.BankConfig config,
                                       String rawBody, String signature) {
        if (!config.isEnabled()) {
            throw new IllegalStateException(provider + " webhook received but is not configured/enabled (banks." +
                    provider.name().toLowerCase() + ".enabled=false)");
        }
        if (config.getSignatureSecret() != null && !config.getSignatureSecret().isBlank()) {
            if (!signatureVerifier.verify(rawBody, signature, config.getSignatureSecret())) {
                throw new SecurityException("Invalid webhook signature for " + provider);
            }
        } else {
            log.warn("{} webhook signature verification is disabled (no signatureSecret configured) — do not run this way in production", provider);
        }

        Map<String, Object> payload = parse(rawBody);

        String accountNumber = extractString(payload, "accountNumber", "AccountNumber", "account_number", "billRefNumber", "customerNumber");
        String transactionRef = extractString(payload, "transactionRef", "TransactionReference", "transactionReference", "reference", "txnId", "transactionId");
        String payerName = extractString(payload, "payerName", "CustomerName", "customerName", "senderName");
        Double amount = extractAmount(payload, "amount", "Amount", "transactionAmount", "TransactionAmount");

        return reconciliationService.reconcile(ReconcileInput.builder()
                .provider(provider)
                .transactionRef(transactionRef)
                .accountReference(accountNumber)
                .amount(amount)
                .payerPhoneOrAccount(accountNumber)
                .payerName(payerName)
                .method(FeePayment.PaymentMethod.BANK_TRANSFER)
                .rawPayload(rawBody)
                .build());
    }

    private Map<String, Object> parse(String rawBody) {
        try {
            return objectMapper.readValue(rawBody, Map.class);
        } catch (Exception e) {
            log.error("Could not parse bank webhook payload as JSON: {}", rawBody, e);
            return Map.of();
        }
    }

    private String extractString(Map<String, Object> payload, String... candidateKeys) {
        for (String key : candidateKeys) {
            Object v = payload.get(key);
            if (v != null && !v.toString().isBlank()) return v.toString().trim();
        }
        return null;
    }

    private Double extractAmount(Map<String, Object> payload, String... candidateKeys) {
        String raw = extractString(payload, candidateKeys);
        if (raw == null) return null;
        try {
            return Double.valueOf(raw.replace(",", ""));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
