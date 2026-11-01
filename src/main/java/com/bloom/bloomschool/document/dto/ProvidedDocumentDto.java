package com.bloom.bloomschool.document.dto;

import com.bloom.bloomschool.document.enums.OwnerType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProvidedDocumentDto {
    private UUID uuid;
    private OwnerType ownerType;
    private UUID ownerUuid;
    private UUID documentTypeUuid;
    private String fileName;
    private String filePath;
    private Long fileSize;
    private LocalDateTime uploadDate;
    private DocumentTypeDto documentTypeDto;
    private String contentType;
    private byte[] fileContent;
}
