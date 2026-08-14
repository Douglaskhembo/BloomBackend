package com.bloom.bloomschool.biometrics.dto.request;

import com.bloom.bloomschool.biometrics.util.FingerName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BioEnrollRequest {

    /** Left-hand fingerprint scan image (PNG/JPEG/BMP/TIFF/WSQ), base64-encoded */
    @NotBlank
    private String leftFingerprintImage;
    @NotNull
    private FingerName leftFingerName;

    /** Right-hand fingerprint scan image, same convention as left */
    @NotBlank
    private String rightFingerprintImage;
    @NotNull
    private FingerName rightFingerName;

    /** Optional face template ref */
    private String faceTemplateRef;

    @NotBlank
    private String enrolledDeviceId;
}
