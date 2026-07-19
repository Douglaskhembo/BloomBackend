package com.bloom.bloomschool.payments.service;

import com.bloom.bloomschool.fees.entity.FeePayment;
import com.bloom.bloomschool.fees.repository.FeePaymentRepository;
import com.bloom.bloomschool.payments.dto.ReconcileInput;
import com.bloom.bloomschool.payments.entity.PaymentTransaction;
import com.bloom.bloomschool.payments.repository.PaymentTransactionRepository;
import com.bloom.bloomschool.students.entity.Student;
import com.bloom.bloomschool.students.repository.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Single place every payment channel (M-Pesa STK, M-Pesa C2B, bank webhooks) funnels through
 * to turn a gateway event into a {@link FeePayment}. Matching is done purely on the account
 * reference the payer typed in against {@link Student#getAdmissionNumber()} — if it doesn't
 * match a known student the transaction is stored as UNMATCHED for a staff member to fix via
 * {@link #manualReconcile}, rather than silently dropped.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentReconciliationService {

    private static final Logger log = LoggerFactory.getLogger(PaymentReconciliationService.class);

    private final PaymentTransactionRepository txRepo;
    private final StudentRepository studentRepo;
    private final FeePaymentRepository feePaymentRepo;

    /** For channels where the first time we see the payment IS the event carrying everything (C2B, bank webhooks). */
    public PaymentTransaction reconcile(ReconcileInput input) {
        if (input.transactionRef() != null) {
            Optional<PaymentTransaction> existing = txRepo.findByTransactionRef(input.transactionRef());
            if (existing.isPresent()) {
                log.info("Duplicate {} callback for transactionRef={} ignored", input.provider(), input.transactionRef());
                return existing.get();
            }
        }

        String accountRef = normalize(input.accountReference());
        PaymentTransaction tx = PaymentTransaction.builder()
                .provider(input.provider())
                .transactionRef(input.transactionRef())
                .accountReference(accountRef)
                .amount(input.amount())
                .payerPhoneOrAccount(input.payerPhoneOrAccount())
                .payerName(input.payerName())
                .rawPayload(input.rawPayload())
                .receivedAt(LocalDateTime.now())
                .status(PaymentTransaction.Status.UNMATCHED)
                .build();

        return finalizeTransaction(tx, accountRef, input.method());
    }

    /** Creates the PENDING record at STK-push time, before the async callback arrives. */
    public PaymentTransaction createPendingStk(String checkoutRequestId, String merchantRequestId,
                                                String accountReference, Double amount, String phone, String rawPayload) {
        PaymentTransaction tx = PaymentTransaction.builder()
                .provider(PaymentTransaction.Provider.MPESA_STK)
                .checkoutRequestId(checkoutRequestId)
                .merchantRequestId(merchantRequestId)
                .accountReference(normalize(accountReference))
                .amount(amount)
                .payerPhoneOrAccount(phone)
                .rawPayload(rawPayload)
                .receivedAt(LocalDateTime.now())
                .status(PaymentTransaction.Status.PENDING)
                .build();
        return txRepo.save(tx);
    }

    /**
     * Completes a previously-pending STK transaction. The account reference is read from the
     * PENDING row saved at initiation — NOT from the callback body, since the STK callback's
     * CallbackMetadata does not reliably include the merchant's AccountReference.
     */
    public PaymentTransaction completeStkTransaction(String checkoutRequestId, String receiptNumber,
                                                       Double amount, String payerPhone, String rawPayload) {
        PaymentTransaction tx = txRepo.findByCheckoutRequestId(checkoutRequestId)
                .orElseThrow(() -> new EntityNotFoundException("No pending STK transaction for checkoutRequestId " + checkoutRequestId));

        if (tx.getStatus() != PaymentTransaction.Status.PENDING) {
            log.info("Ignoring duplicate STK callback for checkoutRequestId={}", checkoutRequestId);
            return tx;
        }

        if (receiptNumber != null && txRepo.existsByTransactionRef(receiptNumber)) {
            log.warn("STK receipt {} already recorded under another transaction, marking duplicate", receiptNumber);
            tx.setStatus(PaymentTransaction.Status.FAILED);
            tx.setFailureReason("Duplicate receipt " + receiptNumber);
            return txRepo.save(tx);
        }

        tx.setTransactionRef(receiptNumber);
        tx.setAmount(amount != null ? amount : tx.getAmount());
        tx.setPayerPhoneOrAccount(payerPhone != null ? payerPhone : tx.getPayerPhoneOrAccount());
        tx.setRawPayload(rawPayload);

        return finalizeTransaction(tx, tx.getAccountReference(), FeePayment.PaymentMethod.MPESA);
    }

    public PaymentTransaction markStkFailed(String checkoutRequestId, String reason, String rawPayload) {
        PaymentTransaction tx = txRepo.findByCheckoutRequestId(checkoutRequestId)
                .orElseThrow(() -> new EntityNotFoundException("No pending STK transaction for checkoutRequestId " + checkoutRequestId));
        tx.setStatus(PaymentTransaction.Status.FAILED);
        tx.setFailureReason(reason);
        tx.setRawPayload(rawPayload);
        return txRepo.save(tx);
    }

    /** Staff-driven fix for an UNMATCHED transaction (typo'd admission number, etc). */
    public PaymentTransaction manualReconcile(Long transactionId, String admissionNumber) {
        PaymentTransaction tx = txRepo.findById(transactionId)
                .orElseThrow(() -> new EntityNotFoundException("Payment transaction not found"));
        if (tx.getStatus() == PaymentTransaction.Status.MATCHED) {
            throw new IllegalArgumentException("Transaction is already matched to a student");
        }
        String accountRef = normalize(admissionNumber);
        tx.setAccountReference(accountRef);
        return finalizeTransaction(tx, accountRef, methodFor(tx.getProvider()));
    }

    private PaymentTransaction finalizeTransaction(PaymentTransaction tx, String accountRef, FeePayment.PaymentMethod method) {
        Optional<Student> student = (accountRef == null || accountRef.isBlank())
                ? Optional.empty()
                : studentRepo.findByAdmissionNumber(accountRef);

        tx.setStatus(student.isPresent() ? PaymentTransaction.Status.MATCHED : PaymentTransaction.Status.UNMATCHED);
        tx.setFailureReason(student.isPresent() ? null : "No student found for account reference '" + accountRef + "'");
        tx = txRepo.save(tx);

        if (student.isPresent()) {
            createFeePayment(tx, student.get(), method);
        } else {
            log.warn("{} transaction {} could not be matched to a student (account reference '{}')",
                    tx.getProvider(), tx.getTransactionRef(), accountRef);
        }
        return tx;
    }

    private void createFeePayment(PaymentTransaction tx, Student student, FeePayment.PaymentMethod method) {
        String reference = tx.getTransactionRef() != null ? tx.getTransactionRef() : "TX-" + tx.getUuid();
        if (feePaymentRepo.existsByReference(reference)) {
            log.info("FeePayment with reference {} already exists, skipping duplicate creation", reference);
            return;
        }
        FeePayment payment = FeePayment.builder()
                .studentId(student.getAdmissionNumber())
                .studentName(student.getFirstName() + " " + student.getLastName())
                .grade(student.getGrade())
                .stream(student.getStream())
                .amount(tx.getAmount() != null ? tx.getAmount() : 0)
                .method(method != null ? method : methodFor(tx.getProvider()))
                .reference(reference)
                .paymentDate(tx.getReceivedAt())
                .build();
        payment = feePaymentRepo.save(payment);
        tx.setMatchedFeePaymentId(payment.getId());
    }

    private FeePayment.PaymentMethod methodFor(PaymentTransaction.Provider provider) {
        return switch (provider) {
            case MPESA_STK, MPESA_C2B -> FeePayment.PaymentMethod.MPESA;
            case EQUITY, KCB, COOP -> FeePayment.PaymentMethod.BANK_TRANSFER;
        };
    }

    private String normalize(String s) { return s == null ? null : s.trim(); }
}
