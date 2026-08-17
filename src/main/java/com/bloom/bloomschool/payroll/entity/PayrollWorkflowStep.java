package com.bloom.bloomschool.payroll.entity;

import com.bloom.bloomschool.auth.model.User;
import com.bloom.bloomschool.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


@Entity
@Table(name = "bloom_sch_payroll_workflow_steps")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PayrollWorkflowStep extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @PrePersist
    public void generateUuid() { if (uuid == null) uuid = UUID.randomUUID(); }

    @Column(nullable = false, unique = true)
    private int sequenceOrder;

    @Column(nullable = false)
    private String label;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ApprovalRule approvalRule = ApprovalRule.ALL_MUST_APPROVE;

    /** Only used when approvalRule == AT_LEAST — how many of the assigned people must approve. */
    private Integer minApprovals;

    /** Step only applies to runs whose total net pay is at least this amount; null/0 = always applies.
     *  Lets bigger payrolls require additional/escalated sign-off (e.g. a CEO step above KES 500,000). */
    private Double thresholdAmount;

    @Builder.Default
    private boolean active = true;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "bloom_sch_payroll_workflow_step_users",
            joinColumns = @JoinColumn(name = "step_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    @Builder.Default @ToString.Exclude @EqualsAndHashCode.Exclude
    private Set<User> assignedUsers = new HashSet<>();

    public enum ApprovalRule { ALL_MUST_APPROVE, ANY_ONE_APPROVES, AT_LEAST }
}
