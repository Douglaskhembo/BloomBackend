package com.bloom.bloomschool.timetable.entity;

import com.bloom.bloomschool.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.UUID;

/** One row of the school-wide daily period grid — shared by every grade/stream so that
 *  "same day + same period" reliably means "same time" for teacher-conflict checks. */
@Entity
@Table(name = "bloom_sch_timetable_periods")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TimetablePeriod extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @PrePersist
    public void generateUuid() { if (uuid == null) uuid = UUID.randomUUID(); }

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Builder.Default
    private boolean breakPeriod = false;

    /** e.g. "BREAK" / "LUNCH" / "TEA BREAK" — only meaningful when breakPeriod is true. */
    private String breakLabel;
}
