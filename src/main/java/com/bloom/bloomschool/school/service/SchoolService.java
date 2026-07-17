package com.bloom.bloomschool.school.service;

import com.bloom.bloomschool.school.dto.*;
import com.bloom.bloomschool.school.entity.*;
import com.bloom.bloomschool.school.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SchoolService {

    private final SchoolInfoRepository schoolInfoRepo;
    private final GradeLevelRepository gradeLevelRepo;
    private final DepartmentRepository departmentRepo;
    private final BranchRepository branchRepo;

    // ── School Info ──────────────────────────────────────────────────────────

    public SchoolInfo getSchoolInfo() {
        return schoolInfoRepo.findAll().stream().findFirst()
                .orElseThrow(() -> new EntityNotFoundException("School info not configured"));
    }

    @Transactional
    public SchoolInfo saveSchoolInfo(SchoolInfoRequest req) {
        SchoolInfo info = schoolInfoRepo.findAll().stream().findFirst()
                .orElse(new SchoolInfo());
        info.setName(req.getName());
        info.setRegistrationNumber(req.getRegistrationNumber());
        info.setEmail(req.getEmail());
        info.setPhone(req.getPhone());
        info.setCounty(req.getCounty());
        info.setSubCounty(req.getSubCounty());
        info.setPostalAddress(req.getPostalAddress());
        info.setPhysicalAddress(req.getPhysicalAddress());
        info.setWebsite(req.getWebsite());
        return schoolInfoRepo.save(info);
    }

    // ── Grade Levels ─────────────────────────────────────────────────────────

    public List<GradeLevel> getAllGradeLevels() {
        return gradeLevelRepo.findAllByOrderByDisplayOrderAsc();
    }

    @Transactional
    public GradeLevel createGradeLevel(GradeLevelRequest req) {
        if (gradeLevelRepo.existsByName(req.getName()))
            throw new IllegalArgumentException("Grade level '" + req.getName() + "' already exists");
        GradeLevel g = GradeLevel.builder()
                .name(req.getName())
                .displayOrder(req.getDisplayOrder())
                .streams(req.getStreams())
                .build();
        return gradeLevelRepo.save(g);
    }

    @Transactional
    public GradeLevel updateGradeLevel(Long id, GradeLevelRequest req) {
        GradeLevel g = gradeLevelRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Grade level not found"));
        g.setName(req.getName());
        g.setDisplayOrder(req.getDisplayOrder());
        g.setStreams(req.getStreams());
        return gradeLevelRepo.save(g);
    }

    @Transactional
    public void toggleGradeLevelStatus(Long id) {
        GradeLevel g = gradeLevelRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Grade level not found"));
        g.setStatus(g.getStatus() == GradeLevel.Status.ACTIVE ? GradeLevel.Status.INACTIVE : GradeLevel.Status.ACTIVE);
        gradeLevelRepo.save(g);
    }

    @Transactional
    public void deleteGradeLevel(Long id) {
        gradeLevelRepo.deleteById(id);
    }

    // ── Departments ──────────────────────────────────────────────────────────

    public List<Department> getAllDepartments() {
        return departmentRepo.findAll();
    }

    @Transactional
    public Department createDepartment(DepartmentRequest req) {
        if (departmentRepo.existsByCode(req.getCode()))
            throw new IllegalArgumentException("Department code '" + req.getCode() + "' already exists");
        Department d = Department.builder()
                .name(req.getName())
                .code(req.getCode().toUpperCase())
                .head(req.getHead())
                .build();
        return departmentRepo.save(d);
    }

    @Transactional
    public Department updateDepartment(Long id, DepartmentRequest req) {
        Department d = departmentRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Department not found"));
        d.setName(req.getName());
        d.setCode(req.getCode().toUpperCase());
        d.setHead(req.getHead());
        return departmentRepo.save(d);
    }

    @Transactional
    public void toggleDepartmentStatus(Long id) {
        Department d = departmentRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Department not found"));
        d.setStatus(d.getStatus() == Department.Status.ACTIVE ? Department.Status.INACTIVE : Department.Status.ACTIVE);
        departmentRepo.save(d);
    }

    @Transactional
    public void deleteDepartment(Long id) {
        departmentRepo.deleteById(id);
    }

    // ── Branches ─────────────────────────────────────────────────────────────

    public List<Branch> getAllBranches() {
        return branchRepo.findAll();
    }

    public Branch getBranch(Long id) {
        return branchRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Branch not found"));
    }

    @Transactional
    public Branch createBranch(BranchRequest req) {
        if (branchRepo.existsByCode(req.getCode()))
            throw new IllegalArgumentException("Branch code '" + req.getCode() + "' already exists");
        Branch b = Branch.builder()
                .name(req.getName())
                .code(req.getCode().toUpperCase())
                .location(req.getLocation())
                .phone(req.getPhone())
                .build();
        applyBranchAssignments(b, req);
        return branchRepo.save(b);
    }

    @Transactional
    public Branch updateBranch(Long id, BranchRequest req) {
        Branch b = branchRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Branch not found"));
        b.setName(req.getName());
        b.setCode(req.getCode().toUpperCase());
        b.setLocation(req.getLocation());
        b.setPhone(req.getPhone());
        applyBranchAssignments(b, req);
        return branchRepo.save(b);
    }

    @Transactional
    public void toggleBranchStatus(Long id) {
        Branch b = branchRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Branch not found"));
        b.setStatus(b.getStatus() == Branch.Status.ACTIVE ? Branch.Status.INACTIVE : Branch.Status.ACTIVE);
        branchRepo.save(b);
    }

    @Transactional
    public void deleteBranch(Long id) {
        branchRepo.deleteById(id);
    }

    private void applyBranchAssignments(Branch b, BranchRequest req) {
        if (req.getDepartmentIds() != null) {
            Set<Department> depts = new HashSet<>(departmentRepo.findAllById(req.getDepartmentIds()));
            b.setDepartments(depts);
        }
        if (req.getGradeLevelIds() != null) {
            Set<GradeLevel> grades = new HashSet<>(gradeLevelRepo.findAllById(req.getGradeLevelIds()));
            b.setGradeLevels(grades);
        }
    }
}
