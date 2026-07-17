package com.bloom.bloomschool.biometrics.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class BioDataResponse {
    private UUID uuid;
    private String ownerUuid;
    private String ownerName;
    private String ownerRef;

    private String leftFingerprintTemplateRef;
    private String leftFingerName;

    private String rightFingerprintTemplateRef;
    private String rightFingerName;

    private String faceTemplateRef;
    private String enrolledDeviceId;
    private LocalDateTime enrolledAt;
    private String status;
}
