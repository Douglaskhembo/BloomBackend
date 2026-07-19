package com.bloom.bloomschool.leave.entity;

import com.bloom.bloomschool.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "bloom_sch_leave_types")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LeaveType extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @PrePersist
    public void generateUuid() { if (uuid == null) uuid = UUID.randomUUID(); }

    @Column(unique = true, nullable = false)
    private String name;

    private int maxDaysPerYear;
    private boolean requiresApproval;
    @Builder.Default
    private boolean active = true;

    @Builder.Default
    private boolean paid = true;

    @Builder.Default
    private boolean requiresDocument = false;

    @ElementCollection
    @CollectionTable(name = "bloom_sch_leave_type_documents", joinColumns = @JoinColumn(name = "leave_type_id"))
    @Column(name = "document_type")
    @Builder.Default
    private List<String> documentTypes = new ArrayList<>();
}
