package com.bloom.bloomschool.users.repository;

import com.bloom.bloomschool.users.entity.Modules;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ModulesRepository extends JpaRepository<Modules, Long> {
    Optional<Modules>findByUuid(UUID uuid);

    Optional<Modules> findByName(String name);

    boolean existsByName(String name);
}
