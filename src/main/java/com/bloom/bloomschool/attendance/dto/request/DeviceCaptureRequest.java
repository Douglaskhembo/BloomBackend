package com.bloom.bloomschool.attendance.dto.request;

import com.bloom.bloomschool.attendance.util.OwnerType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Body a physical scanner (or its bridge/agent software) sends on every scan: just the raw
 * scan image — the device itself is authenticated separately via the X-Device-Code /
 * X-Device-Key headers, but who the fingerprint belongs to is always resolved server-side via
 * 1:N matching (see FingerprintIdentificationService), never taken as a claim from the device.
 */
@Data
public class DeviceCaptureRequest {
    /** Probe fingerprint scan image (PNG/JPEG/BMP/TIFF/WSQ), base64-encoded */
    @NotBlank
    private String image;

    /** Optional — restricts matching to just students or just staff, e.g. a classroom device */
    private OwnerType ownerType;

    private String remarks;
}
