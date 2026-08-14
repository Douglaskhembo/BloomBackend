package com.bloom.bloomschool.leave.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class LeaveBalanceResponse {
    private Long leaveTypeId;
    private UUID leaveTypeUuid;
    private String leaveTypeName;
    private int maxDaysPerYear;
    private double usedDays;
    private double remainingDays;

    private int year;
    private double proratedEntitlement;
    private boolean carryForwardAllowed;
    private double carriedForwardDays;
    private double totalAvailableDays;
}
