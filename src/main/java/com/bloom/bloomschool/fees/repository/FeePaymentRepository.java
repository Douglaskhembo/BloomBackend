package com.bloom.bloomschool.fees.repository;

import com.bloom.bloomschool.fees.entity.FeePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface FeePaymentRepository extends JpaRepository<FeePayment, Long> {
    List<FeePayment> findByStudentIdOrderByPaymentDateDesc(String studentId);
    boolean existsByReference(String reference);

    @Query("SELECT SUM(p.amount) FROM FeePayment p WHERE p.studentId = :studentId AND p.verificationStatus = 'CONFIRMED'")
    Double sumAmountByStudentId(String studentId);

    @Query("SELECT p FROM FeePayment p ORDER BY p.paymentDate DESC")
    List<FeePayment> findAllOrderByDateDesc();

    List<FeePayment> findBySourceAndVerificationStatusOrderByPaymentDateDesc(
            FeePayment.PaymentSource source, FeePayment.VerificationStatus verificationStatus);

    /** Candidate manual entries a later bank webhook might be the streamed confirmation of. */
    List<FeePayment> findByStudentIdAndSourceAndMethodAndVerificationStatus(
            String studentId, FeePayment.PaymentSource source, FeePayment.PaymentMethod method,
            FeePayment.VerificationStatus verificationStatus);

    // Reporting aggregates. Note: FeePayment has no academicYear/term field, only paymentDate —
    // these sums are necessarily cumulative-by-grade/stream, not scoped to a specific term's
    // charges. See FeeReportService for how this is combined with StudentFeeCharge totals.
    @Query("SELECT p.grade, p.stream, SUM(p.amount) FROM FeePayment p WHERE p.verificationStatus = 'CONFIRMED' " +
            "AND (:grade IS NULL OR p.grade = :grade) GROUP BY p.grade, p.stream")
    List<Object[]> sumAmountByGradeStream(String grade);

    @Query("SELECT p.studentId, SUM(p.amount) FROM FeePayment p WHERE p.verificationStatus = 'CONFIRMED' " +
            "AND p.studentId IN :studentIds GROUP BY p.studentId")
    List<Object[]> sumAmountByStudentIds(Collection<String> studentIds);

    @Query(value = "SELECT to_char(payment_date, 'YYYY-MM') AS ym, SUM(amount) FROM bloom_sch_fee_payments " +
            "WHERE verification_status = 'CONFIRMED' AND payment_date >= :since GROUP BY ym ORDER BY ym", nativeQuery = true)
    List<Object[]> sumAmountByMonthSince(LocalDateTime since);
}
