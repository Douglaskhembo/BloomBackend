package com.bloom.bloomschool.school.repository;

import com.bloom.bloomschool.school.entity.SchoolBankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SchoolBankAccountRepository extends JpaRepository<SchoolBankAccount, Long> {
    Optional<SchoolBankAccount> findByUuid(UUID uuid);
    Optional<SchoolBankAccount> findByUseForPayrollTrue();
}
