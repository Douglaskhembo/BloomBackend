package com.bloom.bloomschool.school.dto;

import lombok.*;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class BranchResponseDTO {
    private UUID uuid;
    private String branchName;
    private String branchCode;
    private String phone;
    private String location;
    private boolean active;
}
