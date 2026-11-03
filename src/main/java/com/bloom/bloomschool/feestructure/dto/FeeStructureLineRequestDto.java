package com.bloom.bloomschool.feestructure.dto;

import lombok.*;

import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class FeeStructureLineRequestDto {
    private UUID itemId;
    private boolean enabled;
    private double amount;
}
