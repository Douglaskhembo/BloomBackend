package com.bloom.bloomschool.document.dto;

import com.bloom.bloomschool.document.enums.DocumentCategory;
import lombok.*;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class DocumentTypeRequestDto {
    private String name;
    private String description;
    private Boolean mandatory;
    private Boolean active;
    private DocumentCategory documentCategory;
}