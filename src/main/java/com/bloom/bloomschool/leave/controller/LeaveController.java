package com.bloom.bloomschool.leave.controller;

import com.bloom.bloomschool.auth.service.PermissionResolver;
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
    private final PermissionResolver permissionResolver;

    // ── Leave Types ───────────────────────────────────────────────────────────
    // GET left open — every applicant needs the list of leave types to file a request.

    @GetMapping("/leave-types")
    public ResponseEntity<ApiResponse<?>> getLeaveTypes() {
        return ResponseEntity.ok(ApiResponse.ok(leaveService.getAllLeaveTypes()));
    }

    @PostMapping("/leave-types")
    public ResponseEntity<ApiResponse<?>> createLeaveType(@Valid @RequestBody LeaveTypeRequest req) {
        permissionResolver.requirePermission("LEAVE_MANAGE");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Leave type created", leaveService.createLeaveType(req)));
    }

    @PutMapping("/leave-types/{id}")
    public ResponseEntity<ApiResponse<?>> updateLeaveType(@PathVariable Long id, @Valid @RequestBody LeaveTypeRequest req) {
        permissionResolver.requirePermission("LEAVE_MANAGE");
        return ResponseEntity.ok(ApiResponse.ok("Leave type updated", leaveService.updateLeaveType(id, req)));
    }

    @PatchMapping("/leave-types/{id}/toggle-status")
    public ResponseEntity<ApiResponse<?>> toggleLeaveType(@PathVariable Long id) {
        permissionResolver.requirePermission("LEAVE_MANAGE");
        leaveService.toggleLeaveTypeStatus(id);
        return ResponseEntity.ok(ApiResponse.ok("Status toggled"));
    }

    @DeleteMapping("/leave-types/{id}")
    public ResponseEntity<ApiResponse<?>> deleteLeaveType(@PathVariable Long id) {
        permissionResolver.requirePermission("LEAVE_MANAGE");
        leaveService.deleteLeaveType(id);
        return ResponseEntity.ok(ApiResponse.ok("Leave type deleted"));
    }

    // ── Leave Requests ────────────────────────────────────────────────────────

    /** staffId present = self-service (own requests, any authenticated staff); absent = the full
     *  admin listing, which requires review rights. */
    @GetMapping("/leave-requests")
    public ResponseEntity<ApiResponse<?>> getRequests(@RequestParam(required = false) String staffId) {
        if (staffId != null) return ResponseEntity.ok(ApiResponse.ok(leaveService.getRequestsByStaff(staffId)));
        permissionResolver.requirePermission("LEAVE_APPROVE");
        return ResponseEntity.ok(ApiResponse.ok(leaveService.getAllRequests()));
    }

    @GetMapping("/leave-requests/balance")
    public ResponseEntity<ApiResponse<?>> getBalances(@RequestParam String staffId, @RequestParam(required = false) Integer year) {
        return ResponseEntity.ok(ApiResponse.ok(
                year != null ? leaveService.getBalances(staffId, year) : leaveService.getBalances(staffId)));
    }

    @PostMapping("/leave-requests")
    public ResponseEntity<ApiResponse<?>> createRequest(@Valid @RequestBody LeaveRequestDto req) {
        permissionResolver.requirePermission("LEAVE_APPLY");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Leave request submitted", leaveService.createRequest(req)));
    }

    @PatchMapping("/leave-requests/{id}/review")
    public ResponseEntity<ApiResponse<?>> reviewRequest(
            @PathVariable Long id,
            @RequestParam LeaveRequest.Status status,
            @RequestParam(required = false) String note) {
        permissionResolver.requirePermission("LEAVE_APPROVE");
        return ResponseEntity.ok(ApiResponse.ok("Request reviewed", leaveService.reviewRequest(id, status, note)));
    }

    @DeleteMapping("/leave-requests/{id}")
    public ResponseEntity<ApiResponse<?>> deleteRequest(@PathVariable Long id) {
        permissionResolver.requirePermission("LEAVE_MANAGE");
        leaveService.deleteRequest(id);
        return ResponseEntity.ok(ApiResponse.ok("Request deleted"));
    }
}
