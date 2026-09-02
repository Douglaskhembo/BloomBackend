package com.bloom.bloomschool.calender.dto;

import lombok.*;
import java.util.UUID;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter

public class SchoolEventResponseDTO {
    private UUID uuid;
    private String eventName;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean active;
}
