package com.bloom.bloomschool.feestructure.repository;

import com.bloom.bloomschool.feestructure.entity.FeePayment;
import com.bloom.bloomschool.feestructure.enumerations.FeeVerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.bloom.bloomschool.feestructure.enumerations.FeeVerificationStatus.CONFIRMED;

@Repository
public interface FeePaymentRepository extends JpaRepository<FeePayment, UUID> {

    Optional<FeePayment> findByUuid(UUID uuid);

    boolean existsByReference(String reference);

    @Query("SELECT COALESCE(SUM(fp.amount), 0.0) " +
            "FROM FeePayment fp WHERE " +
            "fp.verificationStatus = com.bloom.bloomschool.feestructure.enumerations.FeeVerificationStatus.CONFIRMED " +
            "AND fp.student.uuid = :studentUuid")
    double sumConfirmedPayments(@Param("studentUuid") UUID studentUuid);

    List<FeePayment> findAllByOrderByPaymentDateDesc();

    Optional<FeePayment> findByStudent_UuidOrderByPaymentDateDesc(UUID studentUuid);

    boolean existsByAdmissionsUuidAndVerificationStatus(UUID admissionsUuid, FeeVerificationStatus feeVerificationStatus);
}
