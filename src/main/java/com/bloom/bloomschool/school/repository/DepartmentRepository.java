package com.bloom.bloomschool.school.repository;

import com.bloom.bloomschool.school.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Optional<Department> findByUuid(UUID uuid);
    List<Department> findAllByUuidIn(Set<UUID> uuids);
    boolean existsByCode(String code);
    boolean existsByStatus(Department.Status status);
}
