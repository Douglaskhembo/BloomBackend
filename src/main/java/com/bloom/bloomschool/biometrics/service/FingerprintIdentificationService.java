package com.bloom.bloomschool.biometrics.service;

import com.bloom.bloomschool.attendance.util.OwnerType;
import com.bloom.bloomschool.biometrics.entity.StaffBioData;
import com.bloom.bloomschool.biometrics.entity.StudentBioData;
import com.bloom.bloomschool.biometrics.repository.StaffBioDataRepository;
import com.bloom.bloomschool.biometrics.repository.StudentBioDataRepository;
import com.bloom.bloomschool.biometrics.util.EnrollmentStatus;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;

/**
 * Runs 1:N fingerprint matching: given a probe scan image, finds the best-scoring enrolled
 * person across ACTIVE students and/or staff, or rejects the scan if nothing clears the
 * configured threshold. This is the single matching engine shared by the web "identify" test
 * path and the real device-capture path — neither ever trusts a caller-supplied identity.
 */
@Service
@Transactional(readOnly = true)
public class FingerprintIdentificationService {

    private final FingerprintEngine engine;
    private final StudentBioDataRepository studentBioDataRepository;
    private final StaffBioDataRepository staffBioDataRepository;
    private final double matchThreshold;

    public FingerprintIdentificationService(FingerprintEngine engine,
                                             StudentBioDataRepository studentBioDataRepository,
                                             StaffBioDataRepository staffBioDataRepository,
                                             @Value("${app.biometrics.match-threshold}") double matchThreshold) {
        this.engine = engine;
        this.studentBioDataRepository = studentBioDataRepository;
        this.staffBioDataRepository = staffBioDataRepository;
        this.matchThreshold = matchThreshold;
    }

    /**
     * @param base64Image raw probe scan image (PNG/JPEG/BMP/TIFF/WSQ), base64-encoded
     * @param ownerTypeHint optional — restricts the search to just students or just staff
     */
    public IdentifiedOwner identify(String base64Image, OwnerType ownerTypeHint) {
        byte[] probeTemplate = engine.extractTemplate(Base64.getDecoder().decode(base64Image));

        IdentifiedOwner best = null;

        if (ownerTypeHint != OwnerType.STAFF) {
            for (StudentBioData candidate : studentBioDataRepository.findByStatus(EnrollmentStatus.ACTIVE)) {
                double score = bestFingerScore(probeTemplate, candidate.getLeftFingerprintTemplate(), candidate.getRightFingerprintTemplate());
                if (best == null || score > best.score()) {
                    best = new IdentifiedOwner(candidate.getUuid(), OwnerType.STUDENT, score);
                }
            }
        }

        if (ownerTypeHint != OwnerType.STUDENT) {
            for (StaffBioData candidate : staffBioDataRepository.findByStatus(EnrollmentStatus.ACTIVE)) {
                double score = bestFingerScore(probeTemplate, candidate.getLeftFingerprintTemplate(), candidate.getRightFingerprintTemplate());
                if (best == null || score > best.score()) {
                    best = new IdentifiedOwner(candidate.getUuid(), OwnerType.STAFF, score);
                }
            }
        }

        if (best == null || best.score() < matchThreshold)
            throw new EntityNotFoundException("No matching fingerprint found");

        return best;
    }

    private double bestFingerScore(byte[] probeTemplate, byte[] leftTemplate, byte[] rightTemplate) {
        return Math.max(engine.score(probeTemplate, leftTemplate), engine.score(probeTemplate, rightTemplate));
    }
}
