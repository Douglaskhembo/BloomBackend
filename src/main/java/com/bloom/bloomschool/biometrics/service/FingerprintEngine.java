package com.bloom.bloomschool.biometrics.service;

import com.machinezoo.sourceafis.FingerprintImage;
import com.machinezoo.sourceafis.FingerprintImageOptions;
import com.machinezoo.sourceafis.FingerprintMatcher;
import com.machinezoo.sourceafis.FingerprintTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Thin wrapper around SourceAFIS (com.machinezoo.sourceafis:sourceafis:3.18.1, pinned — its
 * serialized template format has no cross-version compatibility guarantee, so bumping the
 * version later means re-extracting from source images, not just deserializing old bytes).
 *
 * Only the extracted template is ever persisted, never the original scan image.
 */
@Component
public class FingerprintEngine {

    private final int scannerDpi;

    public FingerprintEngine(@Value("${app.biometrics.scanner-dpi}") int scannerDpi) {
        this.scannerDpi = scannerDpi;
    }

    /** Extracts a compact template from a raw fingerprint image (PNG/JPEG/BMP/TIFF/WSQ bytes). */
    public byte[] extractTemplate(byte[] imageBytes) {
        FingerprintImage image = new FingerprintImage(imageBytes, new FingerprintImageOptions().dpi(scannerDpi));
        return new FingerprintTemplate(image).toByteArray();
    }

    /** Similarity score between a probe and a candidate template — higher means more similar. */
    public double score(byte[] probeTemplateBytes, byte[] candidateTemplateBytes) {
        FingerprintMatcher matcher = new FingerprintMatcher(new FingerprintTemplate(probeTemplateBytes));
        return matcher.match(new FingerprintTemplate(candidateTemplateBytes));
    }
}
