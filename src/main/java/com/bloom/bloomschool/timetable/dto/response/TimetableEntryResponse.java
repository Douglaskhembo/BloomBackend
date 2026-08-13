package com.bloom.bloomschool.timetable.dto.response;

import lombok.*;

import java.time.DayOfWeek;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimetableEntryResponse {
    private UUID uuid;
    private UUID gradeLevelUuid;
    private String grade;
    private String stream;
    private DayOfWeek dayOfWeek;
    private UUID periodUuid;
    private UUID subjectUuid;
    private String subjectName;
    private UUID teacherUuid;
    private String teacherName;
}
