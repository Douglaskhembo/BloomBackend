package com.bloom.bloomschool.attendance.dto.request;

import com.bloom.bloomschool.attendance.util.OwnerType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class DeviceCaptureRequest {
    @NotBlank
    private String image;
    private OwnerType ownerType;
    private String remarks;
}
