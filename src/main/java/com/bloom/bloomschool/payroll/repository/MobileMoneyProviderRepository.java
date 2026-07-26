package com.bloom.bloomschool.payroll.repository;

import com.bloom.bloomschool.payroll.entity.MobileMoneyProvider;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MobileMoneyProviderRepository extends JpaRepository<MobileMoneyProvider, Long> {
    boolean existsByName(String name);
}
