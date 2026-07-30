package com.bloom.bloomschool.staffroles.repository;

import com.bloom.bloomschool.staffroles.entity.StaffRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface StaffRoleRepository extends JpaRepository<StaffRole, Long> {
    boolean existsByName(String name);
    Optional<StaffRole> findByUuid(UUID uuid);
}
