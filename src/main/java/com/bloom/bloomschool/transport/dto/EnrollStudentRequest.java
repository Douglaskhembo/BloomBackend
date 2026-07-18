package com.bloom.bloomschool.transport.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class EnrollStudentRequest {
    @NotNull private UUID studentUuid;
    @NotNull private UUID routeUuid;
    @NotBlank private String pickupPoint;
}
