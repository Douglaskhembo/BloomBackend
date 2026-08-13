package com.bloom.bloomschool.timetable.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.DayOfWeek;
import java.util.UUID;

@Data
public class TimetableEntryRequest {
    @NotNull private UUID gradeLevelUuid;
    private String stream;
    @NotNull private DayOfWeek dayOfWeek;
    @NotNull private UUID periodUuid;
    @NotNull private UUID subjectUuid;
    @NotNull private UUID teacherUuid;
}
