package com.bloom.bloomschool.biometrics.dto.request;

import com.bloom.bloomschool.attendance.util.OwnerType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Body for a fingerprint identification + attendance capture: the caller sends only the
 * scanned image — who it belongs to is determined server-side via 1:N matching, never
 * trusted as a claim from the caller.
 */
@Data
public class BioIdentifyRequest {
    /** Probe fingerprint scan image (PNG/JPEG/BMP/TIFF/WSQ), base64-encoded */
    @NotBlank
    private String image;

    /** Optional — restricts matching to just students or just staff, for speed/scope */
    private OwnerType ownerType;

    @NotBlank
    private String deviceId;

    private String remarks;
}
