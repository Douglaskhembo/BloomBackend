package com.bloom.bloomschool.paymentTest.entity;


import com.bloom.bloomschool.paymentTest.enumerate.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(unique = true, nullable = false, updatable = false)
    private UUID uuid;

//student

    private String phoneNumber;
    private double amount;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    private String checkoutRequestID;
    private String mpesaReceiptNumber;
    private LocalDateTime timeStamp;
    private String accountReference;
}
