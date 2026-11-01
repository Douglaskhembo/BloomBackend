package com.bloom.bloomschool.assessment.entity;

import com.bloom.bloomschool.student.entity.Student;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "bloom_school_assessment_marks",
uniqueConstraints = @UniqueConstraint(columnNames = {"assessment_id", "student_id"}))
public class AssessmentMarks{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @Column(nullable = false)
    private Double score;

    @ManyToOne
    @JoinColumn(name = "assessment_id")
    private Assessment assessment;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;
}
