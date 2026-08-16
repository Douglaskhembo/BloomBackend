package com.bloom.bloomschool.holidays.controller;

import com.bloom.bloomschool.auth.service.PermissionResolver;
import com.bloom.bloomschool.common.dto.ApiResponse;
import com.bloom.bloomschool.holidays.dto.HolidayRequest;
import com.bloom.bloomschool.holidays.service.HolidayService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/holidays")
@RequiredArgsConstructor
public class HolidayController {

    private final HolidayService holidayService;
    private final PermissionResolver permissionResolver;

    // GET left open — public holidays are shown on teacher/parent calendars too, not sensitive.
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(holidayService.getAll()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<?>> create(@Valid @RequestBody HolidayRequest req) {
        permissionResolver.requirePermission("SETUP_MANAGE");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Holiday created", holidayService.create(req)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> update(@PathVariable Long id, @Valid @RequestBody HolidayRequest req) {
        permissionResolver.requirePermission("SETUP_MANAGE");
        return ResponseEntity.ok(ApiResponse.ok("Holiday updated", holidayService.update(id, req)));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<ApiResponse<?>> toggle(@PathVariable Long id) {
        permissionResolver.requirePermission("SETUP_MANAGE");
        return ResponseEntity.ok(ApiResponse.ok("Toggled", holidayService.toggle(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable Long id) {
        permissionResolver.requirePermission("SETUP_MANAGE");
        holidayService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Holiday deleted"));
    }
}
