package com.bloom.bloomschool.feestructure.dto;

import com.bloom.bloomschool.feestructure.enumerations.FeeCategory;
import com.bloom.bloomschool.student.dto.StudentSummaryDto;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class StudentFeeChargeResponseDto {
    private UUID uuid;
    private String admissionNumber;
    private String grade;
    private String stream;
    private int structureVersion;
    private int academicYear;
    private String period;
    private String itemName;
    private FeeCategory feeCategory;
    private double amount;
    private LocalDate dueDate;
    private LocalDateTime generatedAt;
    private StudentSummaryDto student;
    private UUID feeStructureUuid;
    private UUID feeItemUuid;
}
