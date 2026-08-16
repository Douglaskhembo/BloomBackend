package com.bloom.bloomschool.transport.controller;

import com.bloom.bloomschool.auth.service.PermissionResolver;
import com.bloom.bloomschool.common.dto.ApiResponse;
import com.bloom.bloomschool.transport.dto.EnrollStudentRequest;
import com.bloom.bloomschool.transport.dto.RouteRequest;
import com.bloom.bloomschool.transport.service.TransportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/transport")
@RequiredArgsConstructor
public class TransportController {

    private final TransportService transportService;
    private final PermissionResolver permissionResolver;

    // ── Routes ────────────────────────────────────────────────────────────────

    @GetMapping("/routes")
    public ResponseEntity<ApiResponse<?>> getRoutes() {
        return ResponseEntity.ok(ApiResponse.ok(transportService.getAllRoutes()));
    }

    @PostMapping("/routes")
    public ResponseEntity<ApiResponse<?>> createRoute(@Valid @RequestBody RouteRequest req) {
        permissionResolver.requirePermission("TRANSPORT_MANAGE");
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Route created", transportService.createRoute(req)));
    }

    @PutMapping("/routes/{uuid}")
    public ResponseEntity<ApiResponse<?>> updateRoute(@PathVariable UUID uuid, @Valid @RequestBody RouteRequest req) {
        permissionResolver.requirePermission("TRANSPORT_MANAGE");
        return ResponseEntity.ok(ApiResponse.ok("Route updated", transportService.updateRoute(uuid, req)));
    }

    @DeleteMapping("/routes/{uuid}")
    public ResponseEntity<ApiResponse<?>> deleteRoute(@PathVariable UUID uuid) {
        permissionResolver.requirePermission("TRANSPORT_MANAGE");
        transportService.deleteRoute(uuid);
        return ResponseEntity.ok(ApiResponse.ok("Route deleted"));
    }

    @PatchMapping("/routes/{uuid}/toggle-status")
    public ResponseEntity<ApiResponse<?>> toggleStatus(@PathVariable UUID uuid) {
        permissionResolver.requirePermission("TRANSPORT_MANAGE");
        return ResponseEntity.ok(ApiResponse.ok("Status updated", transportService.toggleRouteStatus(uuid)));
    }

    // ── Enrollments ───────────────────────────────────────────────────────────

    @GetMapping("/enrollments")
    public ResponseEntity<ApiResponse<?>> getEnrollments() {
        return ResponseEntity.ok(ApiResponse.ok(transportService.getAllEnrollments()));
    }

    @PostMapping("/enrollments")
    public ResponseEntity<ApiResponse<?>> enroll(@Valid @RequestBody EnrollStudentRequest req) {
        permissionResolver.requirePermission("TRANSPORT_MANAGE");
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Student enrolled", transportService.enrollStudent(req)));
    }

    @DeleteMapping("/enrollments/{uuid}")
    public ResponseEntity<ApiResponse<?>> unenroll(@PathVariable UUID uuid) {
        permissionResolver.requirePermission("TRANSPORT_MANAGE");
        transportService.unenrollStudent(uuid);
        return ResponseEntity.ok(ApiResponse.ok("Student removed from route"));
    }
}
