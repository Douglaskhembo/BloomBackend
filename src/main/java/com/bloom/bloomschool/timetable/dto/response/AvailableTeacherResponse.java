package com.bloom.bloomschool.timetable.dto.response;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailableTeacherResponse {
    private UUID teacherUuid;
    private String teacherName;
    private String staffId;
}
