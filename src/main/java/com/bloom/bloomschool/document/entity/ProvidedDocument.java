package com.bloom.bloomschool.document.entity;

import com.bloom.bloomschool.document.enums.OwnerType;
import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "bloom_school_provided_documents")
public class ProvidedDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false, unique = true)
    private UUID uuid;

    @Enumerated(EnumType.STRING)
    private OwnerType ownerType;

    @Column(nullable = false)
    private UUID ownerUuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_type_uuid", nullable = false)
    private DocumentType documentType;

    private String fileName;

    private String filePath;

    private Long fileSize;

    private LocalDateTime uploadDate;

    private String contentType;
}
