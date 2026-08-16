package com.bloom.bloomschool.fees.repository;

import com.bloom.bloomschool.fees.entity.StudentFeeCharge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface StudentFeeChargeRepository extends JpaRepository<StudentFeeCharge, Long> {
    List<StudentFeeCharge> findByFeeStructureUuid(UUID uuid);
    List<StudentFeeCharge> findByFeeStructureUuidIn(Collection<UUID> uuids);
    List<StudentFeeCharge> findByAdmissionNumberAndFeeStructureUuidIn(String admissionNumber, Collection<UUID> uuids);

    /** A student's full charge history (every period, every academic year) — the basis for
     *  computing a cumulative, FIFO-consistent balance as of any given (academicYear, term)
     *  cutoff. See FeeReportService. */
    List<StudentFeeCharge> findByAdmissionNumberIn(Collection<String> admissionNumbers);
}
