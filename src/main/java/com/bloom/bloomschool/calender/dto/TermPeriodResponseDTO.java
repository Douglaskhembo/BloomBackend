package com.bloom.bloomschool.calender.dto;

import lombok.*;


import java.time.LocalDate;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter

public class TermPeriodResponseDTO {
    private UUID uuid;
    private String term;
    private Integer academicYear;
    private LocalDate startDate;
    private LocalDate endDate;
}
