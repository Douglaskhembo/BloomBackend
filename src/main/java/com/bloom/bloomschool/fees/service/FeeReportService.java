package com.bloom.bloomschool.fees.service;

import com.bloom.bloomschool.fees.dto.FeeArrearsResponse;
import com.bloom.bloomschool.fees.dto.FeeCollectionSummaryResponse;
import com.bloom.bloomschool.fees.dto.FeeCollectionTrendResponse;
import com.bloom.bloomschool.fees.entity.StudentFeeCharge;
import com.bloom.bloomschool.fees.repository.FeePaymentRepository;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeeReportService {

    private final GradeLevelRepository gradeLevelRepository;
    private final StudentFeeChargeRepository studentFeeChargeRepository;
    private final FeePaymentRepository feePaymentRepository;
    private final StudentRepository studentRepository;

    private static final Map<String, Integer> TERM_RANK = Map.of(
            "Term 1", 1, "Term 2", 2, "Term 3", 3, "Full Year", 1);

    public List<FeeCollectionSummaryResponse> getCollectionSummary(int academicYear, String term) {
        List<GradeLevel> grades = gradeLevelRepository.findAllByOrderByDisplayOrderAsc();
        List<Student> roster = studentRepository.findRoster(null, null);
        Map<String, List<StudentFeeCharge>> chargesByAdmission = chargesFor(roster);
        Map<String, Double> paidByAdmission = paidFor(roster);

        Map<String, double[]> agg = new HashMap<>(); // key -> [expected, collected]
        for (Student s : roster) {
            List<StudentFeeCharge> charges = chargesByAdmission.get(s.getAdmissionNumber());
            if (charges == null) continue;
            double billed = cumulativeBilledThrough(charges, academicYear, term);
            if (billed <= 0) continue;
            double paid = Math.min(paidByAdmission.getOrDefault(s.getAdmissionNumber(), 0.0), billed);
            double[] cur = agg.computeIfAbsent(key(s.getGrade(), s.getStream()), k -> new double[2]);
            cur[0] += billed;
            cur[1] += paid;
        }

        List<FeeCollectionSummaryResponse> rows = new ArrayList<>();
        for (GradeLevel g : grades) {
            for (String stream : streamsOf(g)) {
                double[] vals = agg.getOrDefault(key(g.getName(), stream), new double[2]);
                double expected = vals[0], collected = vals[1];
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
        List<Student> roster = studentRepository.findRoster(grade, stream);
        if (roster.isEmpty()) return List.of();

        Map<String, List<StudentFeeCharge>> chargesByAdmission = chargesFor(roster);
        Map<String, Double> paidByAdmission = paidFor(roster);

        List<FeeArrearsResponse> rows = new ArrayList<>();
        for (Student student : roster) {
            List<StudentFeeCharge> charges = chargesByAdmission.get(student.getAdmissionNumber());
            if (charges == null) continue;

            double billed = cumulativeBilledThrough(charges, academicYear, term);
            if (billed <= 0) continue;
            double paid = Math.min(paidByAdmission.getOrDefault(student.getAdmissionNumber(), 0.0), billed);
            double balance = billed - paid;
            if (balance <= 0) continue;

            rows.add(FeeArrearsResponse.builder()
                    .admissionNumber(student.getAdmissionNumber())
                    .studentName(student.getFirstName() + " " + student.getLastName())
                    .grade(student.getGrade())
                    .stream(student.getStream())
                    .parentName(student.getParentName())
                    .parentPhone(student.getParentPhone())
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

    private Map<String, List<StudentFeeCharge>> chargesFor(List<Student> roster) {
        if (roster.isEmpty()) return Map.of();
        List<String> admissionNumbers = roster.stream().map(Student::getAdmissionNumber).toList();
        return studentFeeChargeRepository.findByAdmissionNumberIn(admissionNumbers).stream()
                .collect(Collectors.groupingBy(StudentFeeCharge::getAdmissionNumber));
    }

    private Map<String, Double> paidFor(List<Student> roster) {
        if (roster.isEmpty()) return Map.of();
        List<String> admissionNumbers = roster.stream().map(Student::getAdmissionNumber).toList();
        Map<String, Double> paid = new HashMap<>();
        for (Object[] row : feePaymentRepository.sumAmountByStudentIds(admissionNumbers)) {
            paid.put((String) row[0], ((Number) row[1]).doubleValue());
        }
        return paid;
    }

    private double cumulativeBilledThrough(List<StudentFeeCharge> charges, Integer cutoffYear, String cutoffTerm) {
        return charges.stream()
                .filter(c -> isThroughCutoff(c.getAcademicYear(), c.getPeriod(), cutoffYear, cutoffTerm))
                .mapToDouble(StudentFeeCharge::getAmount)
                .sum();
    }

    private boolean isThroughCutoff(int chargeYear, String chargePeriod, Integer cutoffYear, String cutoffTerm) {
        if (cutoffYear == null) return true;
        if (chargeYear != cutoffYear) return chargeYear < cutoffYear;
        return TERM_RANK.getOrDefault(chargePeriod, 3) <= TERM_RANK.getOrDefault(cutoffTerm, 3);
    }

    private List<String> streamsOf(GradeLevel g) {
        return (g.getStreamNames() == null || g.getStreamNames().isEmpty()) ? List.of("") : g.getStreamNames();
    }

    private String key(String grade, String stream) {
        return grade + "||" + (stream == null ? "" : stream);
    }
}
