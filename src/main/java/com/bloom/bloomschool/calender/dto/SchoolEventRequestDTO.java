package com.bloom.bloomschool.calender.dto;


import lombok.*;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter

public class SchoolEventRequestDTO {
    private String eventName;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean active;
}
