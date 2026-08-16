package com.bloom.bloomschool.fees.repository;

import com.bloom.bloomschool.fees.entity.FeePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface FeePaymentRepository extends JpaRepository<FeePayment, Long> {
    List<FeePayment> findByStudentIdOrderByPaymentDateDesc(String studentId);
    boolean existsByReference(String reference);

    /** Pre-enrollment payments (application/deposit fees) captured against an admission. */
    List<FeePayment> findByAdmissionUuidOrderByPaymentDateDesc(UUID admissionUuid);
    boolean existsByAdmissionUuidAndVerificationStatus(UUID admissionUuid, FeePayment.VerificationStatus verificationStatus);

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

    // Reporting aggregate. Note: FeePayment has no academicYear/term field, only paymentDate —
    // this is necessarily an all-time total per student, not scoped to a specific term. See
    // FeeReportService.isThroughCutoff for how this is combined with cumulative charge totals
    // to derive a FIFO-consistent (oldest period cleared first) balance as of any cutoff.
    @Query("SELECT p.studentId, SUM(p.amount) FROM FeePayment p WHERE p.verificationStatus = 'CONFIRMED' " +
            "AND p.studentId IN :studentIds GROUP BY p.studentId")
    List<Object[]> sumAmountByStudentIds(Collection<String> studentIds);

    @Query(value = "SELECT to_char(payment_date, 'YYYY-MM') AS ym, SUM(amount) FROM bloom_sch_fee_payments " +
            "WHERE verification_status = 'CONFIRMED' AND payment_date >= :since GROUP BY ym ORDER BY ym", nativeQuery = true)
    List<Object[]> sumAmountByMonthSince(LocalDateTime since);
}
