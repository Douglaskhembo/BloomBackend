package com.bloom.bloomschool.payroll.dto;

import com.bloom.bloomschool.payroll.entity.PayrollWorkflowStep;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class PayrollWorkflowStepRequest {
    @NotBlank private String label;
    @NotNull private PayrollWorkflowStep.ApprovalRule approvalRule;
    private boolean active = true;
    private Set<UUID> assignedUserUuids;
    /** Only used when approvalRule == AT_LEAST — how many of the assigned people must approve. */
    private Integer minApprovals;
    /** Only require this step when the run's total net pay is at least this amount; null/0 = always. */
    private Double thresholdAmount;
}
