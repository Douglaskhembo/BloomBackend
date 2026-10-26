package com.bloom.bloomschool.users.repository;

import com.bloom.bloomschool.users.entity.Roles;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Roles, Long> {
    Optional<Roles> findByUuid(UUID uuid);

    Optional<Roles> findByName(String name);

    List<Roles> findAllByUuidIn(List<UUID> uuids);

    boolean existsByName(String name);
}
