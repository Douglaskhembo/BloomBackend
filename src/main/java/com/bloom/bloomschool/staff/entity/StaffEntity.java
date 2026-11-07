package com.bloom.bloomschool.staff.entity;


import com.bloom.bloomschool.gradeLevel.entity.GradeEntity;
import com.bloom.bloomschool.staff.enums.EmploymentType;
import com.bloom.bloomschool.staff.enums.StaffType;
import com.bloom.bloomschool.subject.entity.SubjectEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.*;

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

    @ManyToOne
    @JoinColumn(name = "role_uuid")
    private StaffRole role;

    @Enumerated(EnumType.STRING)
    @Column()
    private EmploymentType employmentType;
    @Builder.Default
    private Boolean isActive = true;

    @ManyToMany
    @JoinTable(
            name = "bloom_staff_subjects",
            joinColumns = @JoinColumn(name = "staff_uuid"),
            inverseJoinColumns = @JoinColumn(name = "subject_uuid")
    )
    @Builder.Default
    private List<SubjectEntity> subjects = new ArrayList<>();
    private LocalDate dateOfBirth;
    private String idNumber;
    private String address;
    private String practiceNumber;
    @Enumerated(EnumType.STRING)
    private StaffType staffType;
    @ManyToMany
    @JoinTable(
            name = "bloom_staff_grades",
            joinColumns = @JoinColumn(name = "staff_uuid"),
            inverseJoinColumns = @JoinColumn(name = "grade_uuid")
    )
    @Builder.Default
    private List<GradeEntity> grades = new ArrayList<>();
    private String qualification;
    private String experience;
    private LocalDate joinedDate;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String emergencyContactRelationship;
    @Builder.Default
    private List<String> documents = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "bloom_staff_documents",
            joinColumns = @JoinColumn(name = "staff_uuid")
    )
    @Column(name = "document_type_uuid", nullable = false)
    private Set<UUID> providedDocumentTypeUuids = new HashSet<>();
}
