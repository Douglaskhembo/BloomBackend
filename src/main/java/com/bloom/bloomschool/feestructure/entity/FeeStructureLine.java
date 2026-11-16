package com.bloom.bloomschool.feestructure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeeStructureLine{
    private UUID itemId;
    private boolean enabled;
    private double amount;
}
