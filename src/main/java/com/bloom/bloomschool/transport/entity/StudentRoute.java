package com.bloom.bloomschool.transport.entity;

import com.bloom.bloomschool.common.entity.BaseEntity;
import com.bloom.bloomschool.students.entity.Student;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "bloom_sch_student_routes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StudentRoute extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @PrePersist
    public void generateUuid() { if (uuid == null) uuid = UUID.randomUUID(); }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @Column(nullable = false)
    private String pickupPoint;

    /**
     * Deactivating (rather than deleting) preserves billing/enrollment history — FeeService only
     * charges the TRANSPORT fee item for active rows, and a deactivated row frees the student up to
     * be re-enrolled later. Uniqueness-per-active-student is enforced by a partial DB index (see
     * migration notes), not a plain column constraint, since a student can have any number of past
     * (inactive) rows but at most one active one.
     */
    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;
}
