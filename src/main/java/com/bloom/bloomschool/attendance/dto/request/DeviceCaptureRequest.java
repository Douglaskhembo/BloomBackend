package com.bloom.bloomschool.attendance.dto.request;

import com.bloom.bloomschool.attendance.util.OwnerType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Body a physical device (or its bridge/agent software) sends on every scan. Identifies the
 * person by a human-readable ref — staffId or admission number — printed on ID cards, since
 * that's realistically what hardware/bridge software can be configured to send; the device
 * itself is authenticated separately via the X-Device-Code / X-Device-Key headers.
 */
@Data
public class DeviceCaptureRequest {
    @NotNull private OwnerType ownerType;
    @NotBlank private String ownerRef;
    private String remarks;
}
