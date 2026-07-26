package com.bloom.bloomschool.school.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class SchoolBankAccountRequest {
    @NotNull private UUID bankUuid;
    @NotBlank private String accountNumber;
    private String accountName;
    private String branch;
}
