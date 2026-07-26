package com.bloom.bloomschool.staff.repository;

import com.bloom.bloomschool.staff.entity.StaffPaymentDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StaffPaymentDetailsRepository extends JpaRepository<StaffPaymentDetails, Long> {
    Optional<StaffPaymentDetails> findByStaffId(String staffId);
}
