package com.bloom.bloomschool.biometrics.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class BioCaptureRequest {
    @NotNull
    private UUID bioDataUuid;
    @NotBlank
    private String deviceId;
    private String remarks;
}
