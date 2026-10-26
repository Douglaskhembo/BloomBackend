package com.bloom.bloomschool.student.entity;

import com.bloom.bloomschool.student.enums.StudentStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(name = "bloom_student")
public class Student {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long studentId;

        @Column(unique = true, nullable = false, updatable = false)
        private UUID uuid;

        @Column(nullable = false, unique = true)
        private String admissionNumber;

        @Column(nullable = false)
        private String firstName;

        @Column(nullable = false)
        private String lastName;

        private LocalDate dateOfBirth;

        @Column(nullable =false, unique = true)
        private String entryNumber;

        private String studentEmail;

        @Enumerated(EnumType.STRING)
        private StudentStatus status;

        @Column(nullable = false, updatable = false)
        private LocalDateTime createdAt;

        private LocalDateTime updatedAt;

        private LocalDate joinDate;
        @PrePersist
        protected void onCreate() {
                this.createdAt = LocalDateTime.now();
                if (this.uuid == null) {
                        this.uuid = UUID.randomUUID();
                }
                if (this.status == null) {
                        this.status = StudentStatus.ACTIVE;
                }
        }

        @PreUpdate
        protected void onUpdate() {

                this.updatedAt = LocalDateTime.now();
        }

        //Keep?
        @ElementCollection(fetch = FetchType.LAZY)
        @CollectionTable(
                name = "bloom_student_documents",
                joinColumns = @JoinColumn(name = "student_uuid")
        )
        @Column(name = "document_type_uuid", nullable = false)
        private Set<UUID> providedDocumentTypeUuids = new HashSet<>();
}