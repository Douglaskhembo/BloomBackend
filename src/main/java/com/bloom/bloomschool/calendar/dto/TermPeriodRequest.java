package com.bloom.bloomschool.calendar.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TermPeriodRequest {
    @NotNull private Integer academicYear;
    @NotBlank private String term;
    @NotNull private LocalDate startDate;
    @NotNull private LocalDate endDate;
}
