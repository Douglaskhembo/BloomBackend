package com.bloom.bloomschool.paymentTest.service;


import com.bloom.bloomschool.paymentTest.entity.Payment;
import com.bloom.bloomschool.paymentTest.enumerate.PaymentStatus;
import com.bloom.bloomschool.paymentTest.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository repo;
    private final MpesaClient mpesaClient;

    public String initiate(String phoneNumber, long amount, String accountReference){
        if (phoneNumber == null){throw new RuntimeException("Phone Number can't be empty");}
        String cleanNumber = phoneNumber.trim();
        if (cleanNumber.startsWith("0")) {cleanNumber = "254" + cleanNumber.substring(1);}
        else if (cleanNumber.startsWith("+")) {cleanNumber = cleanNumber.substring(1);}


        if (amount<1 || amount > 250000){throw new RuntimeException("Invalid amount value");}

        Map<String, Object> responseBody = mpesaClient.initiateStkPush(cleanNumber, String.valueOf(amount), accountReference);

        Payment payment = Payment.builder()
                .phoneNumber(cleanNumber)
                .amount(amount)
                .status(PaymentStatus.valueOf("PENDING"))
                .checkoutRequestID((String) responseBody.get("CheckoutRequestID"))
                .timeStamp(LocalDateTime.now())
                .accountReference(accountReference)
                .build();

        repo.save(payment);
        return ((String) responseBody.get("CheckoutRequestID"));
    }

    
}
