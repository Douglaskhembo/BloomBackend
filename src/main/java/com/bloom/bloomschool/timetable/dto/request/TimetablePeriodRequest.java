package com.bloom.bloomschool.timetable.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalTime;

@Data
public class TimetablePeriodRequest {
    @NotNull private LocalTime startTime;
    @NotNull private LocalTime endTime;
    private boolean breakPeriod;
    private String breakLabel;
}
