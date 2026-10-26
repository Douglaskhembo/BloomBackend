package com.bloom.bloomschool.feestructure.dto;


import com.bloom.bloomschool.feestructure.enumerations.FeeApplicability;
import com.bloom.bloomschool.feestructure.enumerations.FeeCategory;
import com.bloom.bloomschool.gradeLevel.dto.GradeDto;
import com.bloom.bloomschool.gradeLevel.entity.GradeEntity;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeeItemDto {
    private UUID uuid;
    private String name;
    private String description;
    private String term;
    private Double amount;
    private Double termOneAmount;
    private Double termTwoAmount;
    private Double termThreeAmount;
    private Set<GradeDto> gradeLevel = new HashSet<>();
    private List<UUID> gradeUuids;
    private Boolean mandatory;
    private boolean active = true;
    private FeeCategory feeCategory;
    private FeeApplicability feeApplicability;
}
