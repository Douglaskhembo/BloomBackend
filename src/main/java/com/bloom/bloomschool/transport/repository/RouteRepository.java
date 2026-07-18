package com.bloom.bloomschool.transport.repository;

import com.bloom.bloomschool.transport.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RouteRepository extends JpaRepository<Route, Long> {
    Optional<Route> findByUuid(UUID uuid);
}
