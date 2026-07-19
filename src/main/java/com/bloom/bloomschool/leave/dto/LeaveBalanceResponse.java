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
    private int usedDays;
    private int remainingDays;
}
