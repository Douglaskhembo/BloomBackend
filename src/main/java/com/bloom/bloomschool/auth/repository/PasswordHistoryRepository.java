package com.bloom.bloomschool.auth.repository;

import com.bloom.bloomschool.auth.entity.PasswordHistory;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PasswordHistoryRepository extends JpaRepository<PasswordHistory, Long> {
    Optional<PasswordHistory>findByUuid(UUID uuid);
}
