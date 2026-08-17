package com.bloom.bloomschool.common.utils;

import com.bloom.bloomschool.school.entity.Branch;
import com.bloom.bloomschool.school.entity.Department;
import com.bloom.bloomschool.school.entity.SchoolInfo;
import com.bloom.bloomschool.school.repository.BranchRepository;
import com.bloom.bloomschool.school.repository.DepartmentRepository;
import com.bloom.bloomschool.school.repository.SchoolInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@RequiredArgsConstructor
public class SchoolStructureHelper {

    private final SchoolInfoRepository schoolInfoRepo;
    private final BranchRepository branchRepo;
    private final DepartmentRepository deptRepo;

    public SchoolStructureConfig getConfig() {
        List<SchoolInfo> schools = schoolInfoRepo.findAll();
        boolean hasBranches    = branchRepo.existsByStatus(Branch.Status.ACTIVE);
        boolean hasDepartments = deptRepo.existsByStatus(Department.Status.ACTIVE);
        boolean isConfigured   = !schools.isEmpty();
        return new SchoolStructureConfig(isConfigured, hasBranches, hasDepartments);
    }

    public boolean branchesEnabled()    { return getConfig().hasBranches(); }
    public boolean departmentsEnabled() { return getConfig().hasDepartments(); }
    public boolean isSchoolConfigured() { return getConfig().isConfigured(); }

    public record SchoolStructureConfig(boolean isConfigured, boolean hasBranches, boolean hasDepartments) {}
}
