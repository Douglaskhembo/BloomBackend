package com.bloom.bloomschool.paymentTest.entity;

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
public class LedgerEntry {
    //account, debit/credit type, linked payment
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(unique = true, nullable = false, updatable = false)
    private UUID uuid;

    private double amount;
    private LocalDateTime timeStamp;
}
