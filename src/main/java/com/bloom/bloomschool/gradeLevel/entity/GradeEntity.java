package com.bloom.bloomschool.gradeLevel.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "bloom_school_grade")
public class  GradeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column( updatable = false, unique = true, nullable = false)
    private UUID uuid;

    @Column(nullable = false, unique = true)
    private String name;

    private int displayOrder;
    
    private int capacity;

    @Column(nullable = false)
    private Boolean isActive;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "bloom_school_grade_streams",
    joinColumns = @JoinColumn(name = "grade_level_id"))
    @OrderColumn(name = "stream_order")
    @Column(nullable = false)
    @Builder.Default
    private List<String> streamName = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "bloom_school_stream_capacity",
    joinColumns = @JoinColumn(name = "grade_level_id"))
    @OrderColumn(name = "stream_order")
    @Column(nullable = false, name = "capacity")
    @Builder.Default
    private List<Integer> streamCapacity = new ArrayList<>();

    @Column(nullable = false)
    private int streams;

}
