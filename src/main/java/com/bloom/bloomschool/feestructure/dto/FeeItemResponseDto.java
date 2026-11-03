package com.bloom.bloomschool.feestructure.dto;

import com.bloom.bloomschool.feestructure.enumerations.FeeApplicability;
import com.bloom.bloomschool.feestructure.enumerations.FeeCategory;
import com.bloom.bloomschool.gradeLevel.dto.GradeSummaryDto;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class FeeItemResponseDto {
    private UUID uuid;
    private String name;
    private String description;
    private String term;
    private Double amount;
    private Double termOneAmount;
    private Double termTwoAmount;
    private Double termThreeAmount;
    private List<GradeSummaryDto> grades;
    private Boolean mandatory; private boolean active;
    private FeeCategory feeCategory; private FeeApplicability feeApplicability;
}
