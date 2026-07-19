package com.bloom.bloomschool.fees.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FeeStructureReviewRequest {
    @NotBlank private String approver;
    private String reason;
}
