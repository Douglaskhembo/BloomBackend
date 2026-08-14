package com.bloom.bloomschool.fees.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FeeArrearsResponse {
    private String admissionNumber;
    private String studentName;
    private String grade;
    private String stream;
    private String parentName;
    private String parentPhone;
    private double billed;
    private double paid;
    private double balance;
}
