package com.bloom.bloomschool.calender.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "term_periods")
@Entity
public class TermPeriod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long term_id;

    @Column(unique = true, nullable = false, updatable = false)
    private UUID uuid;

    @PrePersist
    public void generateUuid() {
        if (uuid == null) uuid = UUID.randomUUID();
    }

    @Column(nullable = false)
    private String term;

    @Column(nullable = false)
    private Integer academicYear;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

}
