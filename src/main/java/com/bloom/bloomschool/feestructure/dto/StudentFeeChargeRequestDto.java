package com.bloom.bloomschool.feestructure.dto;

import com.bloom.bloomschool.feestructure.enumerations.FeeCategory;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class StudentFeeChargeRequestDto {
    private UUID studentUuid;
    private UUID feeStructureUuid;
    private UUID feeItemUuid;
    private String admissionNumber;
    private String grade;
    private String stream;
    private Integer structureVersion;
    private Integer academicYear;
    private String period;
    private String itemName;
    private FeeCategory feeCategory;
    private Double amount;
    private LocalDate dueDate;
    private LocalDateTime generatedAt;
}
