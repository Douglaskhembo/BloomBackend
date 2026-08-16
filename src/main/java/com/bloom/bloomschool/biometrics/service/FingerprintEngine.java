package com.bloom.bloomschool.biometrics.service;

import com.machinezoo.sourceafis.FingerprintImage;
import com.machinezoo.sourceafis.FingerprintImageOptions;
import com.machinezoo.sourceafis.FingerprintMatcher;
import com.machinezoo.sourceafis.FingerprintTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class FingerprintEngine {

    private final int scannerDpi;

    public FingerprintEngine(@Value("${app.biometrics.scanner-dpi}") int scannerDpi) {
        this.scannerDpi = scannerDpi;
    }

    public byte[] extractTemplate(byte[] imageBytes) {
        FingerprintImage image = new FingerprintImage(imageBytes, new FingerprintImageOptions().dpi(scannerDpi));
        return new FingerprintTemplate(image).toByteArray();
    }

    public double score(byte[] probeTemplateBytes, byte[] candidateTemplateBytes) {
        FingerprintMatcher matcher = new FingerprintMatcher(new FingerprintTemplate(probeTemplateBytes));
        return matcher.match(new FingerprintTemplate(candidateTemplateBytes));
    }
}
