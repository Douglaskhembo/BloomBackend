package com.bloom.bloomschool.feestructure.dto;

import com.bloom.bloomschool.feestructure.enumerations.FeeApplicability;
import com.bloom.bloomschool.feestructure.enumerations.FeeCategory;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class FeeItemRequestDto {
    private String name;
    private String description;
    private String term;
    private Double amount;
    private Double termOneAmount;
    private Double termTwoAmount;
    private Double termThreeAmount;
    private List<UUID> gradeUuids;
    private Boolean mandatory;
    private Boolean active;
    private FeeCategory feeCategory;
    private FeeApplicability feeApplicability;
    }

