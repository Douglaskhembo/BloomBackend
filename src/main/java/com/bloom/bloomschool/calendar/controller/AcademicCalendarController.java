package com.bloom.bloomschool.calendar.controller;

import com.bloom.bloomschool.auth.service.PermissionResolver;
import com.bloom.bloomschool.calendar.dto.SchoolEventRequest;
import com.bloom.bloomschool.calendar.dto.TermPeriodRequest;
import com.bloom.bloomschool.calendar.service.AcademicCalendarService;
import com.bloom.bloomschool.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/academic-calendar")
@RequiredArgsConstructor
public class AcademicCalendarController {

    private final AcademicCalendarService calendarService;
    private final PermissionResolver permissionResolver;

    @GetMapping("/term-periods")
    public ResponseEntity<ApiResponse<?>> getTermPeriods() {
        return ResponseEntity.ok(ApiResponse.ok(calendarService.getAllTermPeriods()));
    }

    @GetMapping("/current-term")
    public ResponseEntity<ApiResponse<?>> getCurrentTerm() {
        return ResponseEntity.ok(ApiResponse.ok(calendarService.getCurrentTerm()));
    }

    @PostMapping("/term-periods")
    public ResponseEntity<ApiResponse<?>> createTermPeriod(@Valid @RequestBody TermPeriodRequest req) {
        permissionResolver.requirePermission("SETUP_MANAGE");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Term period created", calendarService.createTermPeriod(req)));
    }

    @PutMapping("/term-periods/{id}")
    public ResponseEntity<ApiResponse<?>> updateTermPeriod(@PathVariable Long id, @Valid @RequestBody TermPeriodRequest req) {
        permissionResolver.requirePermission("SETUP_MANAGE");
        return ResponseEntity.ok(ApiResponse.ok("Term period updated", calendarService.updateTermPeriod(id, req)));
    }

    @DeleteMapping("/term-periods/{id}")
    public ResponseEntity<ApiResponse<?>> deleteTermPeriod(@PathVariable Long id) {
        permissionResolver.requirePermission("SETUP_MANAGE");
        calendarService.deleteTermPeriod(id);
        return ResponseEntity.ok(ApiResponse.ok("Term period deleted"));
    }

    // ── School Events ────────────────────────────────────────────────────────

    @GetMapping("/events")
    public ResponseEntity<ApiResponse<?>> getEvents() {
        return ResponseEntity.ok(ApiResponse.ok(calendarService.getAllEvents()));
    }

    @PostMapping("/events")
    public ResponseEntity<ApiResponse<?>> createEvent(@Valid @RequestBody SchoolEventRequest req) {
        permissionResolver.requirePermission("SETUP_MANAGE");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("School event created", calendarService.createEvent(req)));
    }

    @PutMapping("/events/{id}")
    public ResponseEntity<ApiResponse<?>> updateEvent(@PathVariable Long id, @Valid @RequestBody SchoolEventRequest req) {
        permissionResolver.requirePermission("SETUP_MANAGE");
        return ResponseEntity.ok(ApiResponse.ok("School event updated", calendarService.updateEvent(id, req)));
    }

    @PatchMapping("/events/{id}/toggle")
    public ResponseEntity<ApiResponse<?>> toggleEvent(@PathVariable Long id) {
        permissionResolver.requirePermission("SETUP_MANAGE");
        return ResponseEntity.ok(ApiResponse.ok("Toggled", calendarService.toggleEvent(id)));
    }

    @DeleteMapping("/events/{id}")
    public ResponseEntity<ApiResponse<?>> deleteEvent(@PathVariable Long id) {
        permissionResolver.requirePermission("SETUP_MANAGE");
        calendarService.deleteEvent(id);
        return ResponseEntity.ok(ApiResponse.ok("School event deleted"));
    }
}
