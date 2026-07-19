package com.bloom.bloomschool.attendance.dto.request;

import com.bloom.bloomschool.attendance.entity.BiometricDevice;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DeviceRequest {
    @NotBlank private String deviceCode;
    @NotBlank private String name;
    private String location;
    @NotNull private BiometricDevice.DeviceType deviceType;
}
