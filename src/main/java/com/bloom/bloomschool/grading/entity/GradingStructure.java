package com.bloom.bloomschool.grading.entity;

import com.bloom.bloomschool.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "bloom_sch_grading_structures")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GradingStructure extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @PrePersist
    public void generateUuid() { if (uuid == null) uuid = UUID.randomUUID(); }

    @Column(nullable = false, unique = true)
    private String grade;

    @ElementCollection
    @CollectionTable(name = "bloom_sch_grading_entries", joinColumns = @JoinColumn(name = "structure_id"))
    @OrderBy("minScore DESC")
    @Builder.Default
    private List<GradingEntry> entries = new ArrayList<>();
}
