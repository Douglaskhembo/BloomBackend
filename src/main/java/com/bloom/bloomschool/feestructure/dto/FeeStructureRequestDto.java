package com.bloom.bloomschool.feestructure.dto;


import com.bloom.bloomschool.feestructure.enumerations.FeeStructureStatus;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class FeeStructureRequestDto {
    private Integer academicYear;
    private String grade;
    private String term;
    private FeeStructureStatus status;
    private Integer version;
    private String comment;
    private String rejectReason;
    private List<FeeStructureLineRequestDto> feeStructureLines;
    private LocalDate dueDate;
    private UUID reworkUuid;
}

