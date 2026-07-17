package com.bloom.bloomschool.auth.repo;

import com.bloom.bloomschool.auth.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByName(String name);
    Optional<Permission> findByUuid(UUID uuid);
    List<Permission> findAllByIdIn(Set<Long> ids);
}
