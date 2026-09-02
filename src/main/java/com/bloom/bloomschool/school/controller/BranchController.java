package com.bloom.bloomschool.school.controller;

import com.bloom.bloomschool.school.dto.BranchRequestDTO;
import com.bloom.bloomschool.school.dto.BranchResponseDTO;
import com.bloom.bloomschool.school.service.BranchService;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/branch")
@RequiredArgsConstructor
public class BranchController {
    private final BranchService branchService;

    // Get all branches
    @GetMapping
    public ResponseEntity<List<BranchResponseDTO>> getAllBranches() {
        return ResponseEntity.ok(branchService.getAllBranches());
    }

    // Get single branch by UUID
    @GetMapping("/{uuid}")
    public ResponseEntity<BranchResponseDTO> getBranchByUuid(@PathVariable UUID uuid) {
        return ResponseEntity.ok(branchService.getBranchByUuid(uuid));
    }

    // Create a branch
    @PostMapping
    public ResponseEntity<?> createBranch(@RequestBody BranchRequestDTO request) {
        branchService.createBranch(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // Update branch data
    @PutMapping("/{uuid}")
    public ResponseEntity<?> update(@PathVariable UUID uuid, @RequestBody BranchRequestDTO req) {
        branchService.updateBranch(uuid, req);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    // Delete branch
    @DeleteMapping("/{uuid}")
    public ResponseEntity<?> deleteBranch(@PathVariable UUID uuid) {
        branchService.deleteBranch(uuid);
        return ResponseEntity.noContent().build();
    }
}