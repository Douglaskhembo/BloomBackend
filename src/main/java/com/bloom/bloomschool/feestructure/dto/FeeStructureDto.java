package com.bloom.bloomschool.feestructure.dto;

import com.bloom.bloomschool.feestructure.enumerations.FeeStructureStatus;
import com.bloom.bloomschool.feestructure.entity.FeeStructureLine;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FeeStructureDto {
    private UUID uuid;
    private int academicYear;
    private String grade;
    private String term;
    private FeeStructureStatus status;
    private int version;
    private String comment;
    private String rejectReason;
    private List<FeeStructureLine> feeStructureLines;
    private List<FeeStructureLine> baseline;

}
