package com.bloom.bloomschool.auth.repo;

import com.bloom.bloomschool.auth.model.SysModule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SysModuleRepository extends JpaRepository<SysModule, Long> {
    Optional<SysModule> findByUuid(UUID uuid);
    Optional<SysModule> findByModuleName(String moduleName);
    boolean existsByModuleName(String moduleName);
}
