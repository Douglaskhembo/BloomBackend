package com.bloom.bloomschool.leave.controller;

import com.bloom.bloomschool.common.dto.ApiResponse;
import com.bloom.bloomschool.leave.dto.LeaveRequestDto;
import com.bloom.bloomschool.leave.dto.LeaveTypeRequest;
import com.bloom.bloomschool.leave.entity.LeaveRequest;
import com.bloom.bloomschool.leave.service.LeaveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;

    // ── Leave Types ───────────────────────────────────────────────────────────

    @GetMapping("/leave-types")
    public ResponseEntity<ApiResponse<?>> getLeaveTypes() {
        return ResponseEntity.ok(ApiResponse.ok(leaveService.getAllLeaveTypes()));
    }

    @PostMapping("/leave-types")
    public ResponseEntity<ApiResponse<?>> createLeaveType(@Valid @RequestBody LeaveTypeRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Leave type created", leaveService.createLeaveType(req)));
    }

    @PutMapping("/leave-types/{id}")
    public ResponseEntity<ApiResponse<?>> updateLeaveType(@PathVariable Long id, @Valid @RequestBody LeaveTypeRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Leave type updated", leaveService.updateLeaveType(id, req)));
    }

    @DeleteMapping("/leave-types/{id}")
    public ResponseEntity<ApiResponse<?>> deleteLeaveType(@PathVariable Long id) {
        leaveService.deleteLeaveType(id);
        return ResponseEntity.ok(ApiResponse.ok("Leave type deleted"));
    }

    // ── Leave Requests ────────────────────────────────────────────────────────

    @GetMapping("/leave-requests")
    public ResponseEntity<ApiResponse<?>> getRequests(@RequestParam(required = false) String staffId) {
        if (staffId != null) return ResponseEntity.ok(ApiResponse.ok(leaveService.getRequestsByStaff(staffId)));
        return ResponseEntity.ok(ApiResponse.ok(leaveService.getAllRequests()));
    }

    @PostMapping("/leave-requests")
    public ResponseEntity<ApiResponse<?>> createRequest(@Valid @RequestBody LeaveRequestDto req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Leave request submitted", leaveService.createRequest(req)));
    }

    @PatchMapping("/leave-requests/{id}/review")
    public ResponseEntity<ApiResponse<?>> reviewRequest(
            @PathVariable Long id,
            @RequestParam LeaveRequest.Status status,
            @RequestParam(required = false) String note) {
        return ResponseEntity.ok(ApiResponse.ok("Request reviewed", leaveService.reviewRequest(id, status, note)));
    }

    @DeleteMapping("/leave-requests/{id}")
    public ResponseEntity<ApiResponse<?>> deleteRequest(@PathVariable Long id) {
        leaveService.deleteRequest(id);
        return ResponseEntity.ok(ApiResponse.ok("Request deleted"));
    }
}
