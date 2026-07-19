package com.bloom.bloomschool.bills.repository;

import com.bloom.bloomschool.bills.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BillRepository extends JpaRepository<Bill, Long> {
    @Query("SELECT b FROM Bill b WHERE LOWER(CONCAT(b.description,' ',b.supplierName)) LIKE LOWER(CONCAT('%',:q,'%'))")
    List<Bill> search(String q);
}
