package com.bloom.bloomschool.payroll.repository;

import com.bloom.bloomschool.payroll.entity.PayeBand;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PayeBandRepository extends JpaRepository<PayeBand, Long> {
    List<PayeBand> findAllByOrderByDisplayOrderAsc();
}
