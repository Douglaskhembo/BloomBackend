package com.bloom.bloomschool.common.utils;

import com.bloom.bloomschool.auth.model.Permission;
import com.bloom.bloomschool.auth.model.Role;
import com.bloom.bloomschool.auth.model.SysModule;
import com.bloom.bloomschool.auth.repo.PermissionRepository;
import com.bloom.bloomschool.auth.repo.RoleRepository;
import com.bloom.bloomschool.auth.repo.SysModuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class SeedService {

    private final SysModuleRepository moduleRepo;
    private final PermissionRepository permissionRepo;
    private final RoleRepository roleRepo;

    @Transactional
    public void seedAll() {
        seedModules();
        seedPermissions();
        seedRolesWithPermissions();
    }

    private void seedModules() {
        if (moduleRepo.count() > 0) { log.info("Modules already seeded, skipping"); return; }
        log.info("Seeding modules...");
        for (String name : List.of(
                "Dashboard Module", "Admin Module", "Students Module", "Academics Module",
                "Finance Module", "Payroll Module", "Leave Module", "Reports Module",
                "Communication Module", "System Setup Module")) {
            moduleRepo.save(SysModule.builder().moduleName(name).build());
        }
        log.info("Modules seeded");
    }

    private void seedPermissions() {
        if (permissionRepo.count() > 0) { log.info("Permissions already seeded, skipping"); return; }
        log.info("Seeding permissions...");

        SysModule dash    = module("Dashboard Module");
        SysModule admin   = module("Admin Module");
        SysModule students= module("Students Module");
        SysModule acad    = module("Academics Module");
        SysModule finance = module("Finance Module");
        SysModule payroll = module("Payroll Module");
        SysModule leave   = module("Leave Module");
        SysModule reports = module("Reports Module");
        SysModule comms   = module("Communication Module");
        SysModule setup   = module("System Setup Module");

        perm("DASHBOARD_VIEW",       "View dashboard",           "READ",   dash);
        perm("DASHBOARD_VIEW_ALL",   "View full admin dashboard","READ",   dash);
        perm("USER_VIEW",            "View users",               "READ",   admin);
        perm("USER_CREATE",          "Create users",             "WRITE",  admin);
        perm("USER_EDIT",            "Edit users",               "WRITE",  admin);
        perm("USER_DELETE",          "Delete users",             "DELETE", admin);
        perm("ROLE_VIEW",            "View roles",               "READ",   admin);
        perm("ROLE_CREATE",          "Create roles",             "WRITE",  admin);
        perm("ROLE_EDIT",            "Edit roles",               "WRITE",  admin);
        perm("ROLE_DELETE",          "Delete roles",             "DELETE", admin);
        perm("ROLE_ASSIGN",          "Assign roles to users",    "WRITE",  admin);
        perm("PERMISSION_ASSIGN",    "Assign permissions",       "WRITE",  admin);
        perm("STUDENT_VIEW",         "View students",            "READ",   students);
        perm("STUDENT_CREATE",       "Admit students",           "WRITE",  students);
        perm("STUDENT_EDIT",         "Edit student records",     "WRITE",  students);
        perm("STUDENT_DELETE",       "Delete students",          "DELETE", students);
        perm("ADMISSION_VIEW",       "View admissions",          "READ",   students);
        perm("ADMISSION_MANAGE",     "Manage admissions",        "WRITE",  students);
        perm("ACADEMICS_VIEW",       "View academics",           "READ",   acad);
        perm("GRADES_VIEW",          "View grades",              "READ",   acad);
        perm("GRADES_ENTER",         "Enter/edit grades",        "WRITE",  acad);
        perm("TIMETABLE_VIEW",       "View timetable",           "READ",   acad);
        perm("TIMETABLE_MANAGE",     "Manage timetable",         "WRITE",  acad);
        perm("SUBJECTS_MANAGE",      "Manage subjects",          "WRITE",  acad);
        perm("REPORTS_VIEW",         "View term reports",        "READ",   acad);
        perm("FEES_VIEW",            "View fee records",         "READ",   finance);
        perm("FEES_MANAGE",          "Manage fee structure",     "WRITE",  finance);
        perm("FEES_COLLECT",         "Record fee payments",      "WRITE",  finance);
        perm("FINANCE_VIEW",         "View finance module",      "READ",   finance);
        perm("FINANCE_MANAGE",       "Manage finance",           "WRITE",  finance);
        perm("SUPPLIERS_MANAGE",     "Manage suppliers",         "WRITE",  finance);
        perm("BILLS_MANAGE",         "Manage bills",             "WRITE",  finance);
        perm("PAYROLL_VIEW",         "View payroll",             "READ",   payroll);
        perm("PAYROLL_RUN",          "Run payroll",              "WRITE",  payroll);
        perm("PAYSLIP_VIEW",         "View own payslip",         "READ",   payroll);
        perm("SALARY_MANAGE",        "Manage staff salaries",    "WRITE",  payroll);
        perm("LEAVE_VIEW",           "View leave requests",      "READ",   leave);
        perm("LEAVE_APPLY",          "Apply for leave",          "WRITE",  leave);
        perm("LEAVE_APPROVE",        "Approve leave",            "WRITE",  leave);
        perm("LEAVE_MANAGE",         "Manage leave types",       "WRITE",  leave);
        perm("REPORT_VIEW",          "View reports",             "READ",   reports);
        perm("REPORT_EXPORT",        "Export reports",           "READ",   reports);
        perm("REPORT_GENERATE",      "Generate reports",         "WRITE",  reports);
        perm("COMMUNICATION_VIEW",   "View messages",            "READ",   comms);
        perm("COMMUNICATION_SEND",   "Send messages",            "WRITE",  comms);
        perm("COMMUNICATION_MANAGE", "Manage communications",    "WRITE",  comms);
        perm("SETUP_VIEW",           "View system setups",       "READ",   setup);
        perm("SETUP_MANAGE",         "Manage system setups",     "WRITE",  setup);
        perm("SCHOOL_SETUP",         "Configure school info",    "WRITE",  setup);

        log.info("Permissions seeded");
    }

    private void seedRolesWithPermissions() {
        if (roleRepo.count() > 0) { log.info("Roles already seeded, skipping"); return; }
        log.info("Seeding roles...");

        List<Permission> all = permissionRepo.findAll();

        roleRepo.save(Role.builder().name("ADMIN")
                .permissions(new HashSet<>(all)).build());

        roleRepo.save(Role.builder().name("TEACHER")
                .permissions(filterByNames(all,
                        "DASHBOARD_VIEW", "ACADEMICS_VIEW", "GRADES_VIEW", "GRADES_ENTER",
                        "TIMETABLE_VIEW", "REPORTS_VIEW", "LEAVE_VIEW", "LEAVE_APPLY",
                        "PAYSLIP_VIEW", "COMMUNICATION_VIEW", "COMMUNICATION_SEND"))
                .build());

        roleRepo.save(Role.builder().name("PARENT")
                .permissions(filterByNames(all,
                        "DASHBOARD_VIEW", "GRADES_VIEW", "REPORTS_VIEW",
                        "FEES_VIEW", "COMMUNICATION_VIEW", "COMMUNICATION_SEND"))
                .build());

        log.info("Roles seeded");
    }

    private Set<Permission> filterByNames(List<Permission> all, String... names) {
        Set<String> nameSet = Set.of(names);
        Set<Permission> result = new HashSet<>();
        all.forEach(p -> { if (nameSet.contains(p.getName())) result.add(p); });
        return result;
    }

    private SysModule module(String name) {
        return moduleRepo.findByModuleName(name)
                .orElseThrow(() -> new IllegalStateException("Module not found: " + name));
    }

    private void perm(String name, String desc, String accessType, SysModule module) {
        permissionRepo.save(Permission.builder()
                .name(name).permDesc(desc).accessType(accessType).module(module).build());
    }
}
