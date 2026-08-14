package com.bloom.bloomschool.attendance.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AttendanceSummaryResponse {
    private String admissionNumber;
    private String name;
    private String grade;
    private String stream;
    private long daysPresent;
    private long totalSchoolDays;
    private double percentage;
}
