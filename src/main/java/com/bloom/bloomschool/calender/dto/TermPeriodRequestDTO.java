package com.bloom.bloomschool.calender.dto;

import lombok.*;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter


public class TermPeriodRequestDTO {
    private String term;
    private Integer academicYear;
    private LocalDate startDate;
    private LocalDate endDate;

}
