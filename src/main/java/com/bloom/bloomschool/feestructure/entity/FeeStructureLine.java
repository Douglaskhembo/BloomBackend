package com.bloom.bloomschool.feestructure.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeeStructureLine{
    private int itemId;
    private boolean enabled;
    private double amount;
}
