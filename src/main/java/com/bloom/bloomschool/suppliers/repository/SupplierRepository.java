package com.bloom.bloomschool.suppliers.repository;

import com.bloom.bloomschool.suppliers.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    @Query("SELECT s FROM Supplier s WHERE LOWER(CONCAT(s.name,' ',COALESCE(s.category,''))) LIKE LOWER(CONCAT('%',:q,'%'))")
    List<Supplier> search(String q);
}
