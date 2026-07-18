package com.bloom.bloomschool.transport.service;

import com.bloom.bloomschool.students.repository.StudentRepository;
import com.bloom.bloomschool.transport.dto.EnrollStudentRequest;
import com.bloom.bloomschool.transport.dto.RouteRequest;
import com.bloom.bloomschool.transport.entity.Route;
import com.bloom.bloomschool.transport.entity.StudentRoute;
import com.bloom.bloomschool.transport.repository.RouteRepository;
import com.bloom.bloomschool.transport.repository.StudentRouteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransportService {

    private final RouteRepository routeRepo;
    private final StudentRouteRepository studentRouteRepo;
    private final StudentRepository studentRepo;

    // ── Routes ────────────────────────────────────────────────────────────────

    public List<Route> getAllRoutes() { return routeRepo.findAll(); }

    public Route getRouteByUuid(UUID uuid) {
        return routeRepo.findByUuid(uuid).orElseThrow(() -> new EntityNotFoundException("Route not found"));
    }

    @Transactional
    public Route createRoute(RouteRequest req) {
        return routeRepo.save(buildRoute(new Route(), req));
    }

    @Transactional
    public Route updateRoute(UUID uuid, RouteRequest req) {
        return routeRepo.save(buildRoute(getRouteByUuid(uuid), req));
    }

    @Transactional
    public void deleteRoute(UUID uuid) {
        Route r = getRouteByUuid(uuid);
        if (studentRouteRepo.findByRouteUuid(uuid).size() > 0)
            throw new IllegalStateException("Cannot delete route with enrolled students");
        routeRepo.deleteById(r.getId());
    }

    @Transactional
    public Route toggleRouteStatus(UUID uuid) {
        Route r = getRouteByUuid(uuid);
        r.setStatus(r.getStatus() == Route.Status.ACTIVE ? Route.Status.INACTIVE : Route.Status.ACTIVE);
        return routeRepo.save(r);
    }

    // ── Enrollments ───────────────────────────────────────────────────────────

    public List<StudentRoute> getAllEnrollments() { return studentRouteRepo.findAll(); }

    @Transactional
    public StudentRoute enrollStudent(EnrollStudentRequest req) {
        if (studentRouteRepo.existsByStudentUuid(req.getStudentUuid()))
            throw new IllegalStateException("Student is already enrolled in a route");
        StudentRoute sr = StudentRoute.builder()
                .student(studentRepo.findByUuid(req.getStudentUuid())
                        .orElseThrow(() -> new EntityNotFoundException("Student not found")))
                .route(getRouteByUuid(req.getRouteUuid()))
                .pickupPoint(req.getPickupPoint())
                .build();
        return studentRouteRepo.save(sr);
    }

    @Transactional
    public void unenrollStudent(UUID enrollmentUuid) {
        StudentRoute sr = studentRouteRepo.findByUuid(enrollmentUuid)
                .orElseThrow(() -> new EntityNotFoundException("Enrollment not found"));
        studentRouteRepo.deleteById(sr.getId());
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private Route buildRoute(Route r, RouteRequest req) {
        r.setName(req.getName());
        r.setDriver(req.getDriver());
        r.setDriverPhone(req.getDriverPhone());
        r.setVehicle(req.getVehicle());
        r.setCapacity(req.getCapacity());
        r.setFare(req.getFare());
        r.setPickupPoints(req.getPickupPoints() != null ? req.getPickupPoints() : List.of());
        if (req.getStatus() != null) r.setStatus(req.getStatus());
        return r;
    }
}
