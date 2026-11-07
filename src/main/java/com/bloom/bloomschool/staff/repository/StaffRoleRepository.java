package com.bloom.bloomschool.staff.repository;

import com.bloom.bloomschool.staff.entity.StaffRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StaffRoleRepository extends JpaRepository<StaffRole, UUID> {
    Optional<StaffRole> findByUuid(UUID role);
}
