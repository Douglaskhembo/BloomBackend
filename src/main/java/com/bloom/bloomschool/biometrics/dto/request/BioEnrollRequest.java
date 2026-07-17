package com.bloom.bloomschool.biometrics.dto.request;

import com.bloom.bloomschool.biometrics.util.FingerName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BioEnrollRequest {

    /** Left-hand fingerprint template ref from the device */
    @NotBlank
    private String leftFingerprintTemplateRef;
    @NotNull
    private FingerName leftFingerName;

    /** Right-hand fingerprint template ref from the device */
    @NotBlank
    private String rightFingerprintTemplateRef;
    @NotNull
    private FingerName rightFingerName;

    /** Optional face template ref */
    private String faceTemplateRef;

    @NotBlank
    private String enrolledDeviceId;
}
