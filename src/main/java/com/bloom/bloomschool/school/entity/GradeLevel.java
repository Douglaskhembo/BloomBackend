package com.bloom.bloomschool.school.entity;

import com.bloom.bloomschool.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "bloom_sch_grade_levels")
public class GradeLevel extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @PrePersist
    public void generateUuid() { if (uuid == null) uuid = UUID.randomUUID(); }

    @Column(nullable = false, unique = true)
    private String name;

    private int displayOrder;
    private int streams;

    /** Only meaningful when streams > 1 — one name per stream (e.g. "East", "West"), in order.
     *  Empty/unused when streams == 1 (a single undivided grade needs no stream names). */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "bloom_sch_grade_level_streams", joinColumns = @JoinColumn(name = "grade_level_id"))
    @Column(name = "stream_name", nullable = false)
    @OrderColumn(name = "stream_order")
    @Builder.Default
    private List<String> streamNames = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Status status = Status.ACTIVE;

    public enum Status { ACTIVE, INACTIVE }
}
