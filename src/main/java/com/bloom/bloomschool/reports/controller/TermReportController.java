package com.bloom.bloomschool.reports.controller;

import com.bloom.bloomschool.auth.service.PermissionResolver;
import com.bloom.bloomschool.common.dto.ApiResponse;
import com.bloom.bloomschool.reports.dto.request.PublishRequest;
import com.bloom.bloomschool.reports.service.TermReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/term-reports")
@RequiredArgsConstructor
public class TermReportController {

    private final TermReportService termReportService;
    private final PermissionResolver permissionResolver;

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAll(
            @RequestParam(required = false) UUID gradeLevelUuid,
            @RequestParam(required = false) String stream,
            @RequestParam String term,
            @RequestParam int year,
            @RequestParam(required = false) String search) {
        permissionResolver.requirePermission("REPORTS_VIEW");
        return ResponseEntity.ok(ApiResponse.ok(termReportService.computeReports(gradeLevelUuid, stream, term, year, search)));
    }

    @GetMapping("/my-class")
    public ResponseEntity<ApiResponse<?>> getMyClass(@RequestParam UUID teacherUuid, @RequestParam String term, @RequestParam int year) {
        permissionResolver.requirePermission("REPORTS_VIEW");
        return ResponseEntity.ok(ApiResponse.ok(termReportService.getMyClass(teacherUuid, term, year)));
    }

    @GetMapping("/my-children")
    public ResponseEntity<ApiResponse<?>> getMyChildren(@RequestParam UUID parentUserUuid, @RequestParam String term, @RequestParam int year) {
        permissionResolver.requirePermission("REPORTS_VIEW");
        return ResponseEntity.ok(ApiResponse.ok(termReportService.getMyChildren(parentUserUuid, term, year)));
    }

    @GetMapping("/{studentUuid}/detail")
    public ResponseEntity<ApiResponse<?>> getDetail(@PathVariable UUID studentUuid, @RequestParam String term, @RequestParam int year) {
        permissionResolver.requirePermission("REPORTS_VIEW");
        return ResponseEntity.ok(ApiResponse.ok(termReportService.getDetail(studentUuid, term, year)));
    }

    @PostMapping("/publish")
    public ResponseEntity<ApiResponse<?>> publish(@Valid @RequestBody PublishRequest req) {
        permissionResolver.requirePermission("REPORT_GENERATE");
        termReportService.publish(req.getGradeLevelUuid(), req.getStream() == null ? "" : req.getStream(), req.getTerm(), req.getYear());
        return ResponseEntity.ok(ApiResponse.ok("Reports published"));
    }
}
