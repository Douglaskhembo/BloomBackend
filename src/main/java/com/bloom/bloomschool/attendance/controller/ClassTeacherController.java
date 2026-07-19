package com.bloom.bloomschool.attendance.controller;

import com.bloom.bloomschool.attendance.dto.request.ClassTeacherRequest;
import com.bloom.bloomschool.attendance.service.ClassTeacherService;
import com.bloom.bloomschool.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/attendance/class-teachers")
@RequiredArgsConstructor
public class ClassTeacherController {

    private final ClassTeacherService classTeacherService;

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(classTeacherService.getAll()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<?>> assign(@Valid @RequestBody ClassTeacherRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Class teacher assigned", classTeacherService.assign(req)));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<ApiResponse<?>> unassign(@PathVariable UUID uuid) {
        classTeacherService.unassign(uuid);
        return ResponseEntity.ok(ApiResponse.ok("Assignment removed"));
    }

    /** Resolves the logged-in teacher's own assignment (frontend passes teacherUuid from AuthContext.profileRef). */
    @GetMapping("/mine")
    public ResponseEntity<ApiResponse<?>> getMine(@RequestParam UUID teacherUuid) {
        return ResponseEntity.ok(ApiResponse.ok(classTeacherService.getMine(teacherUuid)));
    }

    @GetMapping("/mine/roster")
    public ResponseEntity<ApiResponse<?>> getMyRoster(@RequestParam UUID teacherUuid) {
        return ResponseEntity.ok(ApiResponse.ok(classTeacherService.getMyRoster(teacherUuid)));
    }
}
