package com.bloom.bloomschool.staff.entity;


import com.bloom.bloomschool.staff.EmploymentType;
import com.bloom.bloomschool.subject.entity.SubjectEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(
        name = "bloom_school_staff"
)

public class StaffEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column( updatable = false, unique = true, nullable = false)
    private UUID uuid;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(unique = true, nullable = false)
    private String phoneNumber;

    @Column
    private String gender;

    @Column(nullable = false)
    private String role;

    @Enumerated(EnumType.STRING)
    @Column()
    private EmploymentType employmentType;

    @Column()
    private Boolean isActive = true;

    @ManyToMany
    @JoinTable(
            name = "bloom_staff_subjects",
            joinColumns = @JoinColumn(name = "staff_uuid"),
            inverseJoinColumns = @JoinColumn(name = "subject_uuid")
    )
    private List<SubjectEntity> subjects = new ArrayList<>();

}
