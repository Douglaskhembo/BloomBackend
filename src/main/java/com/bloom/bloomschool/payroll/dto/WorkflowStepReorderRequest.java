package com.bloom.bloomschool.payroll.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class WorkflowStepReorderRequest {
    /** Step UUIDs in the desired execution order (first = executes first after submission). */
    @NotEmpty private List<UUID> stepUuids;
}
