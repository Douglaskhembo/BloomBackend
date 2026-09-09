package com.bloom.bloomschool.student.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(name = "bloom_school_student")
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

}