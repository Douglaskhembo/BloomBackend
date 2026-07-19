package com.bloom.bloomschool.attendance.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class DeviceResponse {
    private UUID uuid;
    private String deviceCode;
    private String name;
    private String location;
    private String deviceType;
    private String status;
    private LocalDateTime lastSeenAt;
    /** Only populated once, in the response to device creation — never retrievable again. */
    private String apiKey;
}
