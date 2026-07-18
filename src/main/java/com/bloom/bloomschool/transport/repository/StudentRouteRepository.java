package com.bloom.bloomschool.transport.repository;

import com.bloom.bloomschool.transport.entity.StudentRoute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudentRouteRepository extends JpaRepository<StudentRoute, Long> {
    Optional<StudentRoute> findByUuid(UUID uuid);
    Optional<StudentRoute> findByStudentUuid(UUID studentUuid);
    List<StudentRoute> findByRouteUuid(UUID routeUuid);
    boolean existsByStudentUuid(UUID studentUuid);
}
