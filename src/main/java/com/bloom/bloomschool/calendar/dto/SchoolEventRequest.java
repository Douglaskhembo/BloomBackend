package com.bloom.bloomschool.calendar.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SchoolEventRequest {
    @NotBlank private String name;
    @NotNull private LocalDate startDate;
    @NotNull private LocalDate endDate;
    private boolean active = true;
}
