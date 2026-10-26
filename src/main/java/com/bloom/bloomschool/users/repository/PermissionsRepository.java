package com.bloom.bloomschool.users.repository;

import com.bloom.bloomschool.users.entity.Permissions;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PermissionsRepository extends JpaRepository<Permissions, Long> {
    Optional<Permissions> findByUuid(UUID uuid);
    Optional<Permissions> findByName(String name);
    boolean existsByName(String name);
}
