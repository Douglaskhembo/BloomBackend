package com.bloom.bloomschool.attendance.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ClassTeacherRequest {
    @NotNull private UUID teacherUuid;
    @NotBlank private String grade;
    @NotBlank private String stream;
}
