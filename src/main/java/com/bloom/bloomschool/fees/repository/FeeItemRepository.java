package com.bloom.bloomschool.fees.repository;

import com.bloom.bloomschool.fees.entity.FeeItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeeItemRepository extends JpaRepository<FeeItem, Long> {
    List<FeeItem> findByGradeOrGradeIsNull(String grade);
}
