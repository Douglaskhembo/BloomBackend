package com.bloom.bloomschool.attendance.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ClassTeacherResponse {
    private UUID uuid;
    private UUID teacherUuid;
    private String teacherName;
    private String staffId;
    private String grade;
    private String stream;
}
