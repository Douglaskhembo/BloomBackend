package com.bloom.bloomschool.biometrics.dto.request;

import com.bloom.bloomschool.biometrics.util.FingerName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BioEnrollRequest {

    @NotBlank
    private String leftFingerprintImage;
    @NotNull
    private FingerName leftFingerName;
    @NotBlank
    private String rightFingerprintImage;
    @NotNull
    private FingerName rightFingerName;
    private String faceTemplateRef;

    @NotBlank
    private String enrolledDeviceId;
}
