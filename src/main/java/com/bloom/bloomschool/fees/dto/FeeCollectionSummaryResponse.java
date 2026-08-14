package com.bloom.bloomschool.fees.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FeeCollectionSummaryResponse {
    private String grade;
    private String stream;
    private double expected;
    private double collected;
    private double balance;
    private double collectionPercent;
}
