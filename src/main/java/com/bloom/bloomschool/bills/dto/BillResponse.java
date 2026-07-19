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
    private Long supplierId;
    private String supplierName;
    private String description;
    private double amount;
    private LocalDate dueDate;
    private LocalDateTime paidDate;
    /** UNPAID, PAID, or OVERDUE — OVERDUE is derived (UNPAID + dueDate in the past), never stored as ground truth. */
    private String status;
}
