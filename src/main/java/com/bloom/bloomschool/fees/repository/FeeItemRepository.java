package com.bloom.bloomschool.fees.repository;

import com.bloom.bloomschool.fees.entity.FeeItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeeItemRepository extends JpaRepository<FeeItem, Long> {
}
