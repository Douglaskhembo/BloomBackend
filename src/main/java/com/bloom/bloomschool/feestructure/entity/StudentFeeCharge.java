package com.bloom.bloomschool.feestructure.entity;


import com.bloom.bloomschool.feestructure.enumerations.FeeCategory;
import com.bloom.bloomschool.student.entity.Student;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

//relationship(fee structure, student, feeItem)

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@Table(name = "bloom_school_student_fee_charge")
public class StudentFeeCharge {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    private String admissionNumber;

    private String grade;

    private String stream;

    private int structureVersion;

    private int academicYear;

    private String period;

    private String itemName;

    @Enumerated(EnumType.STRING)
    private FeeCategory feeCategory;

    private double amount;

    private LocalDate dueDate;

    private LocalDateTime generatedAt;

    @ManyToOne
    @JoinColumn(name = "fee_structure_id")
    private FeeStructureEntity feeStructureEntity;

    @ManyToOne
    @JoinColumn(name = "fee_student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "fee_item_id")
    private FeeItemEntity feeItemEntities;

}
