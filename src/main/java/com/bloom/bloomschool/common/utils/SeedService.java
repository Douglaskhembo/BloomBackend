package com.bloom.bloomschool.common.utils;

import com.bloom.bloomschool.auth.model.Permission;
import com.bloom.bloomschool.auth.model.Role;
import com.bloom.bloomschool.auth.model.SysModule;
import com.bloom.bloomschool.auth.model.User;
import com.bloom.bloomschool.auth.model.UserPermission;
import com.bloom.bloomschool.auth.repo.PermissionRepository;
import com.bloom.bloomschool.auth.repo.RoleRepository;
import com.bloom.bloomschool.auth.repo.SysModuleRepository;
import com.bloom.bloomschool.auth.repo.UserPermissionRepository;
import com.bloom.bloomschool.auth.repo.UserRepository;
import com.bloom.bloomschool.payroll.entity.PayeBand;
import com.bloom.bloomschool.payroll.entity.PayrollSettings;
import com.bloom.bloomschool.payroll.entity.StatutoryDeduction;
import com.bloom.bloomschool.payroll.repository.PayeBandRepository;
import com.bloom.bloomschool.payroll.repository.PayrollSettingsRepository;
import com.bloom.bloomschool.payroll.repository.StatutoryDeductionRepository;
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
    private final UserRepository userRepo;
    private final UserPermissionRepository userPermissionRepo;
    private final PayeBandRepository payeBandRepo;
    private final StatutoryDeductionRepository statutoryDeductionRepo;
    private final PayrollSettingsRepository payrollSettingsRepo;

    @Transactional
    public void seedAll() {
        seedModules();
        seedPermissions();
        seedRolesWithPermissions();
        seedPayrollWorkflowPermissions();
        seedPayrollSetup();
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

    /**
     * Adds the payroll approval-workflow permissions even on databases already seeded before these
     * existed — seedPermissions() above only runs once (guarded by permissionRepo.count() > 0), so
     * new permissions introduced later need their own idempotent, always-run seeding step. Also grants
     * them to ADMIN (which is seeded with "all permissions" once, at roleRepo-empty time) and backfills
     * existing ADMIN users' UserPermission rows so login's effective-permissions list picks them up
     * immediately instead of only on new-user onboarding.
     */
    private void seedPayrollWorkflowPermissions() {
        SysModule payroll = moduleRepo.findByModuleName("Payroll Module")
                .orElseGet(() -> moduleRepo.save(SysModule.builder().moduleName("Payroll Module").build()));

        Permission approve = permIfMissing("PAYROLL_APPROVE", "Act on payroll approval workflow steps", "WRITE", payroll);
        Permission workflowManage = permIfMissing("PAYROLL_WORKFLOW_MANAGE", "Configure the payroll approval workflow", "WRITE", payroll);
        Permission sendToBank = permIfMissing("PAYROLL_SEND_TO_BANK", "Mark an approved payroll run as sent to the bank", "WRITE", payroll);
        Permission staffPayment = permIfMissing("PAYROLL_STAFF_PAYMENT_MANAGE", "Manage staff bank/mobile-money payout details", "WRITE", payroll);

        roleRepo.findByName("ADMIN").ifPresent(admin -> {
            boolean changed = false;
            for (Permission p : List.of(approve, workflowManage, sendToBank, staffPayment)) {
                if (admin.getPermissions().add(p)) changed = true;
            }
            if (changed) roleRepo.save(admin);

            for (User user : userRepo.findAll()) {
                if (!user.getRoles().contains(admin)) continue;
                for (Permission p : List.of(approve, workflowManage, sendToBank, staffPayment)) {
                    if (userPermissionRepo.findByUserIdAndPermissionId(user.getId(), p.getId()) == null) {
                        userPermissionRepo.save(UserPermission.builder().user(user).permission(p).overrideType(null).build());
                    }
                }
            }
        });
    }

    private Permission permIfMissing(String name, String desc, String accessType, SysModule module) {
        Permission existing = permissionRepo.findByName(name).orElse(null);
        if (existing != null) return existing;
        Permission saved = permissionRepo.save(Permission.builder().name(name).permDesc(desc).accessType(accessType).module(module).build());
        log.info("Seeded permission '{}'", name);
        return saved;
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

    /**
     * Seeds current (2025) Kenyan statutory payroll defaults so Payroll Setup isn't a blank slate:
     * PAYE bands (2023 Finance Act), NSSF Tier I/II (Feb 2025 limits), Housing Levy 1.5%, and SHIF
     * 2.75% with a KES 300 floor (replacing NHIF, which this app no longer models as a tiered lookup —
     * see StatutoryDeduction.Category.SHIF). Idempotent: only runs against genuinely empty tables, so
     * it never overwrites values an admin has already configured.
     */
    private void seedPayrollSetup() {
        if (payeBandRepo.count() == 0) {
            log.info("Seeding PAYE bands...");
            payeBandRepo.saveAll(List.of(
                    PayeBand.builder().minAmount(0).maxAmount(24000d).rate(10).displayOrder(0).build(),
                    PayeBand.builder().minAmount(24001).maxAmount(32333d).rate(25).displayOrder(24001).build(),
                    PayeBand.builder().minAmount(32334).maxAmount(500000d).rate(30).displayOrder(32334).build(),
                    PayeBand.builder().minAmount(500001).maxAmount(800000d).rate(32.5).displayOrder(500001).build(),
                    PayeBand.builder().minAmount(800001).maxAmount(null).rate(35).displayOrder(800001).build()));
        } else {
            log.info("PAYE bands already seeded, skipping");
        }

        if (statutoryDeductionRepo.count() == 0) {
            log.info("Seeding statutory deductions (NSSF, Housing Levy, SHIF)...");
            statutoryDeductionRepo.saveAll(List.of(
                    StatutoryDeduction.builder()
                            .name("NSSF Tier I").type(StatutoryDeduction.ValueType.PERCENTAGE)
                            .category(StatutoryDeduction.Category.NSSF)
                            .value(6).thresholdAmount(0d).maxAmount(480d)
                            .employerContribution(true).employerValue(6).build(),
                    StatutoryDeduction.builder()
                            .name("NSSF Tier II").type(StatutoryDeduction.ValueType.PERCENTAGE)
                            .category(StatutoryDeduction.Category.NSSF)
                            .value(6).thresholdAmount(8000d).maxAmount(3840d)
                            .employerContribution(true).employerValue(6).build(),
                    StatutoryDeduction.builder()
                            .name("Affordable Housing Levy").type(StatutoryDeduction.ValueType.PERCENTAGE)
                            .category(StatutoryDeduction.Category.HOUSING_LEVY)
                            .value(1.5).maxAmount(null)
                            .employerContribution(true).employerValue(1.5).build(),
                    StatutoryDeduction.builder()
                            .name("SHIF").type(StatutoryDeduction.ValueType.PERCENTAGE)
                            .category(StatutoryDeduction.Category.SHIF)
                            .value(2.75).minAmount(300d).maxAmount(null)
                            .employerContribution(false).build()));
        } else {
            log.info("Statutory deductions already seeded, skipping");
        }

        if (payrollSettingsRepo.count() == 0) {
            log.info("Seeding payroll settings...");
            payrollSettingsRepo.save(PayrollSettings.builder()
                    .personalRelief(2400).insuranceRelief(60000)
                    .payDay(28).paymentMethod("bank_transfer").currency("KES").build());
        } else {
            log.info("Payroll settings already seeded, skipping");
        }
    }
}
