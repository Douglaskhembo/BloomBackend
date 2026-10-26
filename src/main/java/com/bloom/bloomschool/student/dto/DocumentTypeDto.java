package com.bloom.bloomschool.student.dto;

import com.bloom.bloomschool.student.enums.DocumentCategory;
import com.bloom.bloomschool.student.enums.OwnerType;
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
    private OwnerType ownerType;
}
