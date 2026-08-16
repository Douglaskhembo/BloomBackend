package com.bloom.bloomschool.biometrics.dto.request;

import com.bloom.bloomschool.attendance.util.OwnerType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BioIdentifyRequest {
    @NotBlank
    private String image;
    private OwnerType ownerType;

    @NotBlank
    private String deviceId;

    private String remarks;
}
