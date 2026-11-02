package com.bloom.bloomschool.document.entity;


import com.bloom.bloomschool.document.enums.DocumentCategory;
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

    @Builder.Default
    private boolean active = true;

    @Enumerated(EnumType.STRING)
    private DocumentCategory documentCategory;

}
