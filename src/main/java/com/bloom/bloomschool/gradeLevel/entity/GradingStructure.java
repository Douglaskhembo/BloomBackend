package com.bloom.bloomschool.gradeLevel.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "bloom_school_grading_structure")
public class GradingStructure {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, unique = true, nullable = false)
    private UUID uuid;

    @Column
    private String grade;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "bloom_grade_structure_entry",
            joinColumns = @JoinColumn(name = "structure_id"))
    @Builder.Default
    private List<GradingEntry> gradingEntries = new ArrayList<>();

}
