package com.bloom.bloomschool.fees.controller;

import com.bloom.bloomschool.common.dto.ApiResponse;
import com.bloom.bloomschool.fees.service.FeeReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fees/reports")
@RequiredArgsConstructor
public class FeeReportController {

    private final FeeReportService reportService;

    @GetMapping("/collection-summary")
    public ResponseEntity<ApiResponse<?>> collectionSummary(
            @RequestParam int academicYear,
            @RequestParam String term) {
        return ResponseEntity.ok(ApiResponse.ok(reportService.getCollectionSummary(academicYear, term)));
    }

    @GetMapping("/arrears")
    public ResponseEntity<ApiResponse<?>> arrears(
            @RequestParam(required = false) Integer academicYear,
            @RequestParam String term,
            @RequestParam(required = false) String grade,
            @RequestParam(required = false) String stream) {
        return ResponseEntity.ok(ApiResponse.ok(reportService.getArrears(academicYear, term, grade, stream)));
    }

    @GetMapping("/collection-trend")
    public ResponseEntity<ApiResponse<?>> collectionTrend(@RequestParam(defaultValue = "6") int months) {
        return ResponseEntity.ok(ApiResponse.ok(reportService.getCollectionTrend(months)));
    }
}
