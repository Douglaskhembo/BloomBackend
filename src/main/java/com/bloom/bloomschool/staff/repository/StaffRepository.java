package com.bloom.bloomschool.staff.repository;

import com.bloom.bloomschool.staff.entity.StaffEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StaffRepository extends JpaRepository<StaffEntity, UUID> {

    Optional<StaffEntity> findByUuid(UUID Uuid);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);
}
