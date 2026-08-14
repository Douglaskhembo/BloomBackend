package com.bloom.bloomschool.fees.repository;

import com.bloom.bloomschool.fees.entity.StudentFeeCharge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface StudentFeeChargeRepository extends JpaRepository<StudentFeeCharge, Long> {
    List<StudentFeeCharge> findByFeeStructureUuid(UUID uuid);
    List<StudentFeeCharge> findByFeeStructureUuidIn(Collection<UUID> uuids);
    List<StudentFeeCharge> findByAdmissionNumberAndFeeStructureUuidIn(String admissionNumber, Collection<UUID> uuids);
    boolean existsByFeeStructureUuid(UUID uuid);

    @Query("SELECT c.grade, c.stream, SUM(c.amount) FROM StudentFeeCharge c WHERE c.feeStructureUuid IN :structureUuids GROUP BY c.grade, c.stream")
    List<Object[]> sumAmountByGradeStream(Collection<UUID> structureUuids);

    @Query("SELECT c.admissionNumber, SUM(c.amount) FROM StudentFeeCharge c WHERE c.feeStructureUuid IN :structureUuids " +
            "AND (:stream IS NULL OR c.stream = :stream) GROUP BY c.admissionNumber")
    List<Object[]> sumAmountByAdmissionNumber(Collection<UUID> structureUuids, String stream);
}
