package com.bloom.bloomschool.payroll.repository;

import com.bloom.bloomschool.payroll.entity.Bank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankRepository extends JpaRepository<Bank, Long> {
    boolean existsByName(String name);
}
