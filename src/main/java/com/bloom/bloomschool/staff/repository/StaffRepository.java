package com.bloom.bloomschool.staff.repository;

import com.bloom.bloomschool.staff.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StaffRepository extends JpaRepository<Staff, Long> {
    Optional<Staff> findByUuid(UUID uuid);
    Optional<Staff> findByStaffId(String staffId);
    boolean existsByEmail(String email);
    boolean existsByIdNumber(String idNumber);
    List<Staff> findByStaffType(Staff.StaffType staffType);
    long countByStaffType(Staff.StaffType staffType);
    long countByStatus(Staff.Status status);

    @Query("SELECT s FROM Staff s WHERE LOWER(CONCAT(s.firstName,' ',s.lastName,' ',s.staffId)) LIKE LOWER(CONCAT('%',:q,'%'))")
    List<Staff> search(String q);
}
