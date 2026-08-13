package com.bloom.bloomschool.setups.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "bloom_sch_sequence", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"sequence_type", "sequence_date"})
})
public class SystemSequence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String sequenceType;
    private LocalDate sequenceDate;
    private Integer lastSequence;
}
