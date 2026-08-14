package com.bloom.bloomschool.fees.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FeeCollectionTrendResponse {
    private String month; // "YYYY-MM"
    private double collected;
}
