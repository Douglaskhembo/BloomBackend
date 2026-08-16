package com.bloom.bloomschool.assessments.controller;

import com.bloom.bloomschool.assessments.dto.request.AssessmentRequest;
import com.bloom.bloomschool.assessments.dto.request.MarkEntryRequest;
import com.bloom.bloomschool.assessments.service.AssessmentService;
import com.bloom.bloomschool.auth.service.PermissionResolver;
import com.bloom.bloomschool.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/assessments")
@RequiredArgsConstructor
public class AssessmentController {

    private final AssessmentService assessmentService;
    private final PermissionResolver permissionResolver;

    @GetMapping("/my-classes")
    public ResponseEntity<ApiResponse<?>> getMyClasses(@RequestParam UUID teacherUuid) {
        permissionResolver.requirePermission("GRADES_VIEW");
        return ResponseEntity.ok(ApiResponse.ok(assessmentService.getMyClasses(teacherUuid)));
    }

    @GetMapping("/subject-performance")
    public ResponseEntity<ApiResponse<?>> getSubjectPerformance(@RequestParam String term, @RequestParam int year) {
        permissionResolver.requirePermission("REPORTS_VIEW");
        return ResponseEntity.ok(ApiResponse.ok(assessmentService.getSubjectPerformance(term, year)));
    }

    /** Unscoped class listing — admin browsing any class, or a class teacher's homeroom across
     *  every subject (not just the ones they personally teach). */
    @GetMapping("/all-classes")
    public ResponseEntity<ApiResponse<?>> getAllClasses() {
        permissionResolver.requirePermission("GRADES_VIEW");
        return ResponseEntity.ok(ApiResponse.ok(assessmentService.getAllClasses()));
    }

    /** Every assessment for a class+subject, regardless of who created it. */
    @GetMapping("/for-class")
    public ResponseEntity<ApiResponse<?>> getClassAssessments(
            @RequestParam UUID gradeLevelUuid, @RequestParam(defaultValue = "") String stream, @RequestParam UUID subjectUuid) {
        permissionResolver.requirePermission("GRADES_VIEW");
        return ResponseEntity.ok(ApiResponse.ok(assessmentService.getClassAssessments(gradeLevelUuid, stream, subjectUuid)));
    }

    @GetMapping("/roster")
    public ResponseEntity<ApiResponse<?>> getRoster(@RequestParam UUID gradeLevelUuid, @RequestParam(defaultValue = "") String stream) {
        permissionResolver.requirePermission("GRADES_VIEW");
        return ResponseEntity.ok(ApiResponse.ok(assessmentService.getRoster(gradeLevelUuid, stream)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getMyAssessments(
            @RequestParam UUID teacherUuid,
            @RequestParam UUID gradeLevelUuid,
            @RequestParam(defaultValue = "") String stream,
            @RequestParam UUID subjectUuid) {
        permissionResolver.requirePermission("GRADES_VIEW");
        return ResponseEntity.ok(ApiResponse.ok(assessmentService.getMyAssessments(teacherUuid, gradeLevelUuid, stream, subjectUuid)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<?>> create(@Valid @RequestBody AssessmentRequest req) {
        permissionResolver.requirePermission("GRADES_ENTER");
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Assessment created", assessmentService.createAssessment(req)));
    }

    @GetMapping("/{uuid}/marks")
    public ResponseEntity<ApiResponse<?>> getMarks(@PathVariable UUID uuid) {
        permissionResolver.requirePermission("GRADES_VIEW");
        return ResponseEntity.ok(ApiResponse.ok(assessmentService.getMarks(uuid)));
    }

    @PutMapping("/{uuid}/marks")
    public ResponseEntity<ApiResponse<?>> saveMarks(@PathVariable UUID uuid, @Valid @RequestBody MarkEntryRequest req) {
        permissionResolver.requirePermission("GRADES_ENTER");
        assessmentService.saveMarks(uuid, req);
        return ResponseEntity.ok(ApiResponse.ok("Marks saved"));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable UUID uuid) {
        permissionResolver.requirePermission("GRADES_ENTER");
        assessmentService.deleteAssessment(uuid);
        return ResponseEntity.ok(ApiResponse.ok("Assessment deleted"));
    }
}
