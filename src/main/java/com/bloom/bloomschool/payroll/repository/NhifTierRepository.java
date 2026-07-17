package com.bloom.bloomschool.payroll.repository;

import com.bloom.bloomschool.payroll.entity.NhifTier;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NhifTierRepository extends JpaRepository<NhifTier, Long> {
    List<NhifTier> findAllByOrderByDisplayOrderAsc();
}
