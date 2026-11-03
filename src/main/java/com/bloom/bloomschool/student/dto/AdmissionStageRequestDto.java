package com.bloom.bloomschool.student.dto;

import com.bloom.bloomschool.student.enums.ApplicationStage;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AdmissionStageRequestDto {
    private ApplicationStage stage;
    private LocalDate joinDate;
}
