package com.bloom.bloomschool.bills.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class BillResponse {
    private Long id;
    private UUID uuid;
    private String billNumber;
    private Long supplierId;
    private String supplierName;
    private String description;
    private double amount;
    private LocalDate dueDate;
    private LocalDateTime paidDate;
    private String status;
    private String paymentRef;
    private boolean deleted;
}
