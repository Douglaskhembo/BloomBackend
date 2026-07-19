package com.bloom.bloomschool.fees.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FeeStructureLine {
    private Long itemId;
    private boolean enabled;
    private double amount;
}
