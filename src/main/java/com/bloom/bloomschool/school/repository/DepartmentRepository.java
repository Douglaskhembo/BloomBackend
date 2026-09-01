package com.bloom.bloomschool.school.repository;

import com.bloom.bloomschool.school.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    Optional<Department> findByUuid(UUID uuid);

    List<Department> findAll();

    @Query("SELECT COUNT(d) > 0 FROM Department d WHERE d.departmentCode = ?1")
    boolean existsByDepartmentCode(String departmentCode);

    @Query("SELECT COUNT(d) > 0 FROM Department d WHERE d.departmentName = ?1")
    boolean existsByDepartmentName(String departmentName);

    List<Department> findAllByUuidIn(List<UUID> uuids);
}