package com.bloom.bloomschool.school.service;

import com.bloom.bloomschool.school.dto.*;
import com.bloom.bloomschool.school.entity.Branch;
import com.bloom.bloomschool.school.entity.Department;
import com.bloom.bloomschool.school.repository.BranchRepository;
import com.bloom.bloomschool.school.repository.DepartmentRepository;
import com.bloom.bloomschool.student.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BranchService {
    private final BranchRepository branchRepo;
    private final DepartmentRepository departmentRepo;

    private Branch findEntity(UUID uuid) {
        return branchRepo.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));
    }

    public List<BranchResponseDTO> getAllBranches() {
        return branchRepo.findAll().stream()
                .map(b -> BranchResponseDTO.builder()
                        .uuid(b.getUuid())
                        .branchName(b.getBranchName())
                        .branchCode(b.getBranchCode())
                        .phone(b.getPhone())
                        .location(b.getLocation())
                        .active(b.isActive())
                        .build())
                .toList();
    }

    public BranchResponseDTO getBranchByUuid(UUID uuid) {
        Branch b = findEntity(uuid);
        return BranchResponseDTO.builder()
                .uuid(b.getUuid())
                .branchName(b.getBranchName())
                .branchCode(b.getBranchCode())
                .phone(b.getPhone())
                .location(b.getLocation())
                .active(b.isActive())
                .build();
    }

    @Transactional
    public void createBranch(BranchRequestDTO req) {
        if (branchRepo.existsByCode(req.getBranchCode())) {
            throw new RuntimeException("Branch with this code already exists!!");
        }
        if (branchRepo.existsByName(req.getBranchName())) {
            throw new RuntimeException("Branch with this name already exists!!");
        }
        if (branchRepo.existsByPhone(req.getPhone())){
            throw new RuntimeException("Branch with this phone number already exists!!");
        }
        branchRepo.save(Branch.builder()
                .branchName(req.getBranchName())
                .branchCode(req.getBranchCode())
                .phone(req.getPhone())
                .location(req.getLocation())
                .active(true)
                .departments(convert(req.getDepartmentsUuid()))
                .build());
    }

    private List<Department> convert(List<UUID> uuids){
        if (uuids == null || uuids.isEmpty()) return new ArrayList<>();
        return new ArrayList<>(departmentRepo.findAllByUuidIn(uuids));
    }

    @Transactional
    public void updateBranch(UUID uuid, BranchRequestDTO req) {
        Branch b = findEntity(uuid);
        b.setBranchName(req.getBranchName());
        b.setBranchCode(req.getBranchCode());
        b.setPhone(req.getPhone());
        b.setLocation(req.getLocation());

        branchRepo.save(b);
    }

    @Transactional
    public void deleteBranch(UUID uuid) {
        Branch b = findEntity(uuid);
        branchRepo.delete(b);
    }
}