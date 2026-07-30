package com.bloom.bloomschool.fees.repository;

import com.bloom.bloomschool.fees.entity.FeePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
}
