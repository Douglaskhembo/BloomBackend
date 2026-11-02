package com.bloom.bloomschool.setup.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@Table(name = "bloom_school_system_sequence")
public class SystemSequence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String sequenceType;
    private LocalDate sequenceDate;
    private int lastSequence;
}
