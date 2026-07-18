package com.bloom.bloomschool.transport.entity;

import com.bloom.bloomschool.common.entity.BaseEntity;
import com.bloom.bloomschool.students.entity.Student;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "bloom_sch_student_routes", uniqueConstraints = @UniqueConstraint(columnNames = "student_id"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StudentRoute extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @PrePersist
    public void generateUuid() { if (uuid == null) uuid = UUID.randomUUID(); }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @Column(nullable = false)
    private String pickupPoint;
}
