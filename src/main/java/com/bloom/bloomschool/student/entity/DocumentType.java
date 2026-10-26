package com.bloom.bloomschool.student.entity;


import com.bloom.bloomschool.student.enums.DocumentCategory;
import com.bloom.bloomschool.student.enums.OwnerType;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@Table(name = "bloom_school_document_type")
public class DocumentType {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false, unique = true)
    private UUID uuid;

    @Column(nullable = false)
    private String name;

    private String description;

    private boolean mandatory;

    private boolean active;

    @Enumerated(EnumType.STRING)
    private DocumentCategory documentCategory;

    @Enumerated(EnumType.STRING)
    private OwnerType ownerType;

}
