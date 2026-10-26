package com.bloom.bloomschool.feestructure.dto;


import com.bloom.bloomschool.feestructure.entity.FeeItemEntity;
import com.bloom.bloomschool.feestructure.entity.FeeStructureEntity;
import com.bloom.bloomschool.feestructure.enumerations.FeeCategory;
import com.bloom.bloomschool.student.dto.StudentRequestDTO;
import com.bloom.bloomschool.student.entity.Student;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class StudentFeeChargeDto {
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
    private FeeStructureEntity feeStructureEntity;
    private Student student;
    private FeeItemEntity feeItemEntities;
    private FeeStructureDto feeStructureDto;
    private StudentRequestDTO studentRequestDTO;
    private FeeItemDto feeItemDto;

}
