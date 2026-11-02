package com.bloom.bloomschool.document.dto;

import com.bloom.bloomschool.document.enums.DocumentCategory;
import lombok.*;

import java.util.UUID;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class DocumentTypeDto {
    private UUID uuid;
    private String name;
    private String description;
    private boolean mandatory;
    private boolean active;
    private DocumentCategory documentCategory;
}
