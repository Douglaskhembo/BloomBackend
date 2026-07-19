package com.bloom.bloomschool.payments.service;

import com.bloom.bloomschool.fees.entity.FeePayment;
import com.bloom.bloomschool.payments.client.MpesaClient;
import com.bloom.bloomschool.payments.dto.MpesaC2BRequest;
import com.bloom.bloomschool.payments.dto.MpesaStkCallbackDto;
import com.bloom.bloomschool.payments.dto.ReconcileInput;
import com.bloom.bloomschool.payments.dto.StkPushRequest;
import com.bloom.bloomschool.payments.entity.PaymentTransaction;
import com.bloom.bloomschool.students.entity.Student;
import com.bloom.bloomschool.students.repository.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MpesaService {

    private static final Logger log = LoggerFactory.getLogger(MpesaService.class);

    private final MpesaClient mpesaClient;
    private final StudentRepository studentRepo;
    private final PaymentReconciliationService reconciliationService;

    /** Admin/parent-triggered: sends the STK prompt to the payer's phone. */
    public PaymentTransaction initiateStkPush(StkPushRequest req) {
        Student student = studentRepo.findByAdmissionNumber(req.getAdmissionNumber())
                .orElseThrow(() -> new EntityNotFoundException("No student with admission number '" + req.getAdmissionNumber() + "'"));

        String phone = formatPhone(req.getPhone() != null && !req.getPhone().isBlank() ? req.getPhone() : student.getParentPhone());
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("No phone number available for this student — pass one explicitly or set the parent's phone on the student record");
        }

        Map<String, Object> response = mpesaClient.stkPush(phone, req.getAmount(), req.getAdmissionNumber());
        Object responseCode = response != null ? response.get("ResponseCode") : null;
        if (response == null || !"0".equals(String.valueOf(responseCode))) {
            String desc = response != null ? String.valueOf(response.get("ResponseDescription")) : "no response from M-Pesa";
            throw new IllegalStateException("STK push was not accepted by M-Pesa: " + desc);
        }

        String checkoutRequestId = String.valueOf(response.get("CheckoutRequestID"));
        String merchantRequestId = String.valueOf(response.get("MerchantRequestID"));

        return reconciliationService.createPendingStk(checkoutRequestId, merchantRequestId,
                req.getAdmissionNumber(), req.getAmount(), phone, String.valueOf(response));
    }

    public void handleStkCallback(MpesaStkCallbackDto dto) {
        MpesaStkCallbackDto.StkCallback callback = dto.getBody() != null ? dto.getBody().getStkCallback() : null;
        if (callback == null || callback.getCheckoutRequestID() == null) {
            log.warn("Received malformed STK callback payload: {}", dto);
            return;
        }

        if (callback.getResultCode() != 0) {
            reconciliationService.markStkFailed(callback.getCheckoutRequestID(), callback.getResultDesc(), String.valueOf(dto));
            return;
        }

        String receipt = null, phone = null;
        BigDecimal amount = null;
        List<MpesaStkCallbackDto.Item> items = callback.getCallbackMetadata() != null
                ? callback.getCallbackMetadata().getItem() : List.of();
        for (MpesaStkCallbackDto.Item item : items) {
            if (item.getName() == null || item.getValue() == null) continue;
            switch (item.getName()) {
                case "MpesaReceiptNumber" -> receipt = item.getValue().toString();
                case "PhoneNumber" -> phone = item.getValue().toString();
                case "Amount" -> amount = new BigDecimal(item.getValue().toString());
            }
        }

        reconciliationService.completeStkTransaction(callback.getCheckoutRequestID(), receipt,
                amount != null ? amount.doubleValue() : null, phone, String.valueOf(dto));
    }

    /** C2B validation — called by Safaricom before crediting the paybill; we just confirm the account exists. */
    public boolean isKnownAccount(String billRefNumber) {
        return billRefNumber != null && studentRepo.existsByAdmissionNumber(billRefNumber.trim());
    }

    /** C2B confirmation — payment has already landed on the paybill, this reconciles it. */
    public PaymentTransaction handleC2BConfirmation(MpesaC2BRequest req) {
        Double amount = req.getTransAmount() != null ? Double.valueOf(req.getTransAmount()) : null;
        String payerName = String.join(" ",
                nullToEmpty(req.getFirstName()), nullToEmpty(req.getMiddleName()), nullToEmpty(req.getLastName())).trim();

        return reconciliationService.reconcile(ReconcileInput.builder()
                .provider(PaymentTransaction.Provider.MPESA_C2B)
                .transactionRef(req.getTransID())
                .accountReference(req.getBillRefNumber())
                .amount(amount)
                .payerPhoneOrAccount(req.getMSISDN())
                .payerName(payerName.isBlank() ? null : payerName)
                .method(FeePayment.PaymentMethod.MPESA)
                .rawPayload(String.valueOf(req))
                .build());
    }

    private String nullToEmpty(String s) { return s == null ? "" : s; }

    private String formatPhone(String phone) {
        if (phone == null) return null;
        String p = phone.trim().replace(" ", "");
        if (p.startsWith("0")) return "254" + p.substring(1);
        if (p.startsWith("+254")) return p.substring(1);
        if (p.startsWith("254")) return p;
        return p;
    }
}
