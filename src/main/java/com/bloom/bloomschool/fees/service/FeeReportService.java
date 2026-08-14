package com.bloom.bloomschool.fees.service;

import com.bloom.bloomschool.fees.dto.FeeArrearsResponse;
import com.bloom.bloomschool.fees.dto.FeeCollectionSummaryResponse;
import com.bloom.bloomschool.fees.dto.FeeCollectionTrendResponse;
import com.bloom.bloomschool.fees.entity.FeeStructure;
import com.bloom.bloomschool.fees.repository.FeePaymentRepository;
import com.bloom.bloomschool.fees.repository.FeeStructureRepository;
import com.bloom.bloomschool.fees.repository.StudentFeeChargeRepository;
import com.bloom.bloomschool.school.entity.GradeLevel;
import com.bloom.bloomschool.school.repository.GradeLevelRepository;
import com.bloom.bloomschool.students.entity.Student;
import com.bloom.bloomschool.students.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Aggregation reports over the existing fee data (structures/charges/payments) — no new
 * schema, just group-by queries. Note: {@code FeePayment} has no academicYear/term field
 * (only paymentDate), so "collected" figures are cumulative by grade/stream, not verified
 * against a specific term's charges — see repository query comments for detail.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeeReportService {

    private final GradeLevelRepository gradeLevelRepository;
    private final FeeStructureRepository feeStructureRepository;
    private final StudentFeeChargeRepository studentFeeChargeRepository;
    private final FeePaymentRepository feePaymentRepository;
    private final StudentRepository studentRepository;

    public List<FeeCollectionSummaryResponse> getCollectionSummary(int academicYear, String term) {
        List<GradeLevel> grades = gradeLevelRepository.findAllByOrderByDisplayOrderAsc();
        List<UUID> structureUuids = resolveApprovedStructureUuids(grades, term, academicYear);

        Map<String, Double> expectedByKey = new HashMap<>();
        if (!structureUuids.isEmpty()) {
            for (Object[] row : studentFeeChargeRepository.sumAmountByGradeStream(structureUuids)) {
                expectedByKey.put(key((String) row[0], (String) row[1]), ((Number) row[2]).doubleValue());
            }
        }
        Map<String, Double> collectedByKey = new HashMap<>();
        for (Object[] row : feePaymentRepository.sumAmountByGradeStream(null)) {
            collectedByKey.put(key((String) row[0], (String) row[1]), ((Number) row[2]).doubleValue());
        }

        List<FeeCollectionSummaryResponse> rows = new ArrayList<>();
        for (GradeLevel g : grades) {
            for (String stream : streamsOf(g)) {
                String k = key(g.getName(), stream);
                double expected = expectedByKey.getOrDefault(k, 0.0);
                double collected = collectedByKey.getOrDefault(k, 0.0);
                rows.add(FeeCollectionSummaryResponse.builder()
                        .grade(g.getName())
                        .stream(stream)
                        .expected(expected)
                        .collected(collected)
                        .balance(expected - collected)
                        .collectionPercent(expected > 0 ? (collected / expected) * 100 : 0)
                        .build());
            }
        }
        return rows;
    }

    public List<FeeArrearsResponse> getArrears(Integer academicYear, String term, String grade, String stream) {
        List<GradeLevel> grades = gradeLevelRepository.findAllByOrderByDisplayOrderAsc().stream()
                .filter(g -> grade == null || g.getName().equals(grade))
                .toList();
        List<UUID> structureUuids = resolveApprovedStructureUuids(grades, term, academicYear);
        if (structureUuids.isEmpty()) return List.of();

        Map<String, Double> chargedByAdmission = new HashMap<>();
        for (Object[] row : studentFeeChargeRepository.sumAmountByAdmissionNumber(structureUuids, stream)) {
            chargedByAdmission.put((String) row[0], ((Number) row[1]).doubleValue());
        }
        if (chargedByAdmission.isEmpty()) return List.of();

        Map<String, Double> paidByAdmission = new HashMap<>();
        for (Object[] row : feePaymentRepository.sumAmountByStudentIds(chargedByAdmission.keySet())) {
            paidByAdmission.put((String) row[0], ((Number) row[1]).doubleValue());
        }

        List<FeeArrearsResponse> rows = new ArrayList<>();
        for (var entry : chargedByAdmission.entrySet()) {
            double billed = entry.getValue();
            double paid = paidByAdmission.getOrDefault(entry.getKey(), 0.0);
            double balance = billed - paid;
            if (balance <= 0) continue;

            Student student = studentRepository.findByAdmissionNumber(entry.getKey()).orElse(null);
            rows.add(FeeArrearsResponse.builder()
                    .admissionNumber(entry.getKey())
                    .studentName(student != null ? student.getFirstName() + " " + student.getLastName() : null)
                    .grade(student != null ? student.getGrade() : null)
                    .stream(student != null ? student.getStream() : null)
                    .parentName(student != null ? student.getParentName() : null)
                    .parentPhone(student != null ? student.getParentPhone() : null)
                    .billed(billed)
                    .paid(paid)
                    .balance(balance)
                    .build());
        }
        rows.sort(Comparator.comparingDouble(FeeArrearsResponse::getBalance).reversed());
        return rows;
    }

    public List<FeeCollectionTrendResponse> getCollectionTrend(int months) {
        LocalDateTime since = LocalDate.now().minusMonths(Math.max(months, 1) - 1L).withDayOfMonth(1).atStartOfDay();
        return feePaymentRepository.sumAmountByMonthSince(since).stream()
                .map(row -> FeeCollectionTrendResponse.builder()
                        .month((String) row[0])
                        .collected(((Number) row[1]).doubleValue())
                        .build())
                .toList();
    }

    /** Latest APPROVED structure per grade for the given term/year — reuses the existing
     *  single-grade lookup FeeService.getCurrentCharges relies on, just applied across grades. */
    private List<UUID> resolveApprovedStructureUuids(List<GradeLevel> grades, String term, Integer academicYear) {
        List<UUID> uuids = new ArrayList<>();
        for (GradeLevel g : grades) {
            feeStructureRepository.findFirstByGradeAndTermAndStatusOrderByReviewedAtDesc(g.getName(), term, FeeStructure.Status.APPROVED)
                    .filter(s -> academicYear == null || s.getAcademicYear() == academicYear)
                    .ifPresent(s -> uuids.add(s.getUuid()));
        }
        return uuids;
    }

    private List<String> streamsOf(GradeLevel g) {
        return (g.getStreamNames() == null || g.getStreamNames().isEmpty()) ? List.of("") : g.getStreamNames();
    }

    private String key(String grade, String stream) {
        return grade + "||" + (stream == null ? "" : stream);
    }
}
