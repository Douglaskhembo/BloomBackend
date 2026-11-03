package com.bloom.bloomschool.document.dto;

import com.bloom.bloomschool.document.enums.OwnerType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class ProvidedDocumentResponseDto {
    private UUID uuid;
    private OwnerType ownerType;
    private UUID ownerUuid;
    private DocumentTypeResponseDto documentType;
    private String fileName;
    private Long fileSize;
    private String filePath;
    private LocalDateTime uploadDate;
    private String contentType;
}
