package com.bloom.bloomschool.feestructure.dto;

import com.bloom.bloomschool.feestructure.enumerations.FeeStructureStatus;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class FeeStructureResponseDto {
    private UUID uuid;
    private int academicYear;
    private String grade;
    private String term;
    private FeeStructureStatus status;
    private int version;
    private String comment;
    private String rejectReason;
    private List<FeeStructureLineResponseDto> feeStructureLines;
    private List<FeeStructureLineResponseDto> baseline;
}
