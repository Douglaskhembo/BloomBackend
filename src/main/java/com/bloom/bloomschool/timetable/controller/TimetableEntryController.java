package com.bloom.bloomschool.timetable.controller;

import com.bloom.bloomschool.auth.service.PermissionResolver;
import com.bloom.bloomschool.common.dto.ApiResponse;
import com.bloom.bloomschool.timetable.dto.request.TimetableEntryRequest;
import com.bloom.bloomschool.timetable.service.TimetableEntryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.util.UUID;

@RestController
@RequestMapping("/timetable/entries")
@RequiredArgsConstructor
public class TimetableEntryController {

    private final TimetableEntryService entryService;
    private final PermissionResolver permissionResolver;

    @GetMapping("/grid")
    public ResponseEntity<ApiResponse<?>> getGrid(@RequestParam UUID gradeLevelUuid, @RequestParam(required = false) String stream) {
        return ResponseEntity.ok(ApiResponse.ok(entryService.getGrid(gradeLevelUuid, stream)));
    }

    @GetMapping("/mine")
    public ResponseEntity<ApiResponse<?>> getMine(@RequestParam UUID teacherUuid) {
        return ResponseEntity.ok(ApiResponse.ok(entryService.getMine(teacherUuid)));
    }

    @GetMapping("/available-teachers")
    public ResponseEntity<ApiResponse<?>> getAvailableTeachers(
            @RequestParam UUID subjectUuid,
            @RequestParam DayOfWeek dayOfWeek,
            @RequestParam UUID periodUuid,
            @RequestParam(required = false) UUID excludeEntryUuid) {
        return ResponseEntity.ok(ApiResponse.ok(entryService.getAvailableTeachers(subjectUuid, dayOfWeek, periodUuid, excludeEntryUuid)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<?>> assign(@Valid @RequestBody TimetableEntryRequest req) {
        permissionResolver.requirePermission("TIMETABLE_MANAGE");
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Timetable slot assigned", entryService.assign(req)));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<ApiResponse<?>> unassign(@PathVariable UUID uuid) {
        permissionResolver.requirePermission("TIMETABLE_MANAGE");
        entryService.unassign(uuid);
        return ResponseEntity.ok(ApiResponse.ok("Timetable slot cleared"));
    }
}
