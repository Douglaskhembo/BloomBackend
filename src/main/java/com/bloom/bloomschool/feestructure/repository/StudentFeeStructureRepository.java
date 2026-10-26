package com.bloom.bloomschool.feestructure.repository;

import com.bloom.bloomschool.feestructure.entity.StudentFeeCharge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentFeeStructureRepository extends JpaRepository<StudentFeeCharge, UUID> {
    Optional<StudentFeeCharge> findByUuid(UUID uuid);
}
