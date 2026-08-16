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
import com.bloom.bloomschool.payroll.repository.PayeBandRepository;
import com.bloom.bloomschool.payroll.repository.PayrollSettingsRepository;
import com.bloom.bloomschool.payroll.repository.StatutoryDeductionRepository;
import com.bloom.bloomschool.school.repository.GradeLevelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
    private final GradeLevelRepository gradeLevelRepo;
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void seedAll() {
        seedModules();
        seedPermissions();
        seedRolesWithPermissions();
        seedPayrollWorkflowPermissions();
        seedFeeApprovalPermission();
        seedFeesEnforcementBackfill();
        seedRemainingEnforcementBackfill();
        seedPayrollSetup();
        seedGradeLevels();
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
        perm("FEES_APPROVE",         "Approve or reject submitted fee structures", "WRITE", finance);
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

    /**
     * Adds FEES_APPROVE even on databases already seeded before fee-structure approval enforcement
     * existed — same rationale and pattern as seedPayrollWorkflowPermissions() above: seedPermissions()
     * only runs once (guarded by permissionRepo.count() > 0), so this new permission needs its own
     * idempotent, always-run seeding step to reach existing ADMIN role holders.
     */
    private void seedFeeApprovalPermission() {
        SysModule finance = moduleRepo.findByModuleName("Finance Module")
                .orElseGet(() -> moduleRepo.save(SysModule.builder().moduleName("Finance Module").build()));

        Permission approve = permIfMissing("FEES_APPROVE", "Approve or reject submitted fee structures", "WRITE", finance);

        roleRepo.findByName("ADMIN").ifPresent(admin -> {
            if (admin.getPermissions().add(approve)) roleRepo.save(admin);

            for (User user : userRepo.findAll()) {
                if (!user.getRoles().contains(admin)) continue;
                if (userPermissionRepo.findByUserIdAndPermissionId(user.getId(), approve.getId()) == null) {
                    userPermissionRepo.save(UserPermission.builder().user(user).permission(approve).overrideType(null).build());
                }
            }
        });
    }

    /**
     * FEES_VIEW/FEES_MANAGE/FEES_COLLECT existed in the permission catalog from the start but were
     * never actually enforced by FeeController until now — turning enforcement on for a
     * previously-open endpoint risks locking out any role/user who relied on that openness, so
     * this backfills ADMIN (and existing ADMIN-role users' UserPermission rows, so login picks it
     * up immediately — same rationale as seedPayrollWorkflowPermissions) exactly like every other
     * "permission added/enforced after initial seeding" case in this file. Also mints the new
     * FEES_VERIFY_PAYMENT permission (kept distinct from FEES_APPROVE, which is specifically about
     * the fee-structure maker/approver chain, not day-to-day manual-payment verification).
     */
    private void seedFeesEnforcementBackfill() {
        SysModule finance = moduleRepo.findByModuleName("Finance Module")
                .orElseGet(() -> moduleRepo.save(SysModule.builder().moduleName("Finance Module").build()));

        Permission view = permIfMissing("FEES_VIEW", "View fee records", "READ", finance);
        Permission manage = permIfMissing("FEES_MANAGE", "Manage fee structure", "WRITE", finance);
        Permission collect = permIfMissing("FEES_COLLECT", "Record fee payments", "WRITE", finance);
        Permission verifyPayment = permIfMissing("FEES_VERIFY_PAYMENT", "Verify or reject manually-captured fee payments", "WRITE", finance);

        roleRepo.findByName("ADMIN").ifPresent(admin -> {
            boolean changed = false;
            for (Permission p : List.of(view, manage, collect, verifyPayment)) {
                if (admin.getPermissions().add(p)) changed = true;
            }
            if (changed) roleRepo.save(admin);

            for (User user : userRepo.findAll()) {
                if (!user.getRoles().contains(admin)) continue;
                for (Permission p : List.of(view, manage, collect, verifyPayment)) {
                    if (userPermissionRepo.findByUserIdAndPermissionId(user.getId(), p.getId()) == null) {
                        userPermissionRepo.save(UserPermission.builder().user(user).permission(p).overrideType(null).build());
                    }
                }
            }
        });

        // PARENT already relied on FEES_VIEW (to see their own children's fee statements) before
        // enforcement existed to test it — backfill the same way so no parent is locked out.
        roleRepo.findByName("PARENT").ifPresent(parent -> {
            if (parent.getPermissions().add(view)) roleRepo.save(parent);
            for (User user : userRepo.findAll()) {
                if (!user.getRoles().contains(parent)) continue;
                if (userPermissionRepo.findByUserIdAndPermissionId(user.getId(), view.getId()) == null) {
                    userPermissionRepo.save(UserPermission.builder().user(user).permission(view).overrideType(null).build());
                }
            }
        });
    }

    /**
     * Backend enforcement was added to almost every remaining previously-open controller in one
     * pass (Users/Roles/Permissions, Students/Admissions, Staff, Subjects/Grading, Academic
     * Calendar setup, Leave, Communication, Bills, Suppliers, Transport, Holidays/Staff Roles/
     * School Setup, Attendance reports/devices/class-teachers, Fee reports, Payment
     * reconciliation, and the remaining Payroll setup screens) — 2026-08-16. Most of the
     * permission names used (USER_*, ROLE_*, PERMISSION_ASSIGN, STUDENT_*, ADMISSION_*,
     * SUBJECTS_MANAGE, LEAVE_*, COMMUNICATION_*, BILLS_MANAGE, SUPPLIERS_MANAGE, SETUP_MANAGE,
     * SCHOOL_SETUP, SALARY_MANAGE) already existed in the original catalog but were never
     * actually checked by any controller; three module/permission pairs are genuinely new
     * (STAFF_VIEW/STAFF_MANAGE, ATTENDANCE_VIEW/ATTENDANCE_MANAGE, TRANSPORT_VIEW/TRANSPORT_MANAGE
     * — there was no Staff/Attendance/Transport permission concept at all before). Same rationale
     * and pattern as seedFeesEnforcementBackfill: turning on enforcement for a previously-open
     * endpoint must not lock out anyone already using it, so this backfills ADMIN (role +
     * per-user UserPermission rows) for every permission name now actually enforced.
     */
    private void seedRemainingEnforcementBackfill() {
        SysModule admin = moduleRepo.findByModuleName("Admin Module")
                .orElseGet(() -> moduleRepo.save(SysModule.builder().moduleName("Admin Module").build()));
        SysModule students = moduleRepo.findByModuleName("Students Module")
                .orElseGet(() -> moduleRepo.save(SysModule.builder().moduleName("Students Module").build()));
        SysModule acad = moduleRepo.findByModuleName("Academics Module")
                .orElseGet(() -> moduleRepo.save(SysModule.builder().moduleName("Academics Module").build()));
        SysModule leave = moduleRepo.findByModuleName("Leave Module")
                .orElseGet(() -> moduleRepo.save(SysModule.builder().moduleName("Leave Module").build()));
        SysModule comms = moduleRepo.findByModuleName("Communication Module")
                .orElseGet(() -> moduleRepo.save(SysModule.builder().moduleName("Communication Module").build()));
        SysModule finance = moduleRepo.findByModuleName("Finance Module")
                .orElseGet(() -> moduleRepo.save(SysModule.builder().moduleName("Finance Module").build()));
        SysModule payroll = moduleRepo.findByModuleName("Payroll Module")
                .orElseGet(() -> moduleRepo.save(SysModule.builder().moduleName("Payroll Module").build()));
        SysModule setup = moduleRepo.findByModuleName("System Setup Module")
                .orElseGet(() -> moduleRepo.save(SysModule.builder().moduleName("System Setup Module").build()));
        SysModule attendance = moduleRepo.findByModuleName("Attendance Module")
                .orElseGet(() -> moduleRepo.save(SysModule.builder().moduleName("Attendance Module").build()));
        SysModule transport = moduleRepo.findByModuleName("Transport Module")
                .orElseGet(() -> moduleRepo.save(SysModule.builder().moduleName("Transport Module").build()));

        List<Permission> toBackfill = List.of(
                // Already existed in the catalog, now actually enforced for the first time
                permIfMissing("USER_VIEW", "View users", "READ", admin),
                permIfMissing("USER_CREATE", "Create users", "WRITE", admin),
                permIfMissing("USER_EDIT", "Edit users", "WRITE", admin),
                permIfMissing("USER_DELETE", "Delete users", "DELETE", admin),
                permIfMissing("ROLE_VIEW", "View roles", "READ", admin),
                permIfMissing("ROLE_CREATE", "Create roles", "WRITE", admin),
                permIfMissing("ROLE_EDIT", "Edit roles", "WRITE", admin),
                permIfMissing("ROLE_DELETE", "Delete roles", "DELETE", admin),
                permIfMissing("ROLE_ASSIGN", "Assign roles to users", "WRITE", admin),
                permIfMissing("PERMISSION_ASSIGN", "Assign permissions", "WRITE", admin),
                permIfMissing("STUDENT_CREATE", "Admit students", "WRITE", students),
                permIfMissing("STUDENT_EDIT", "Edit student records", "WRITE", students),
                permIfMissing("STUDENT_DELETE", "Delete students", "DELETE", students),
                permIfMissing("ADMISSION_VIEW", "View admissions", "READ", students),
                permIfMissing("ADMISSION_MANAGE", "Manage admissions", "WRITE", students),
                permIfMissing("SUBJECTS_MANAGE", "Manage subjects", "WRITE", acad),
                permIfMissing("LEAVE_APPLY", "Apply for leave", "WRITE", leave),
                permIfMissing("LEAVE_APPROVE", "Approve leave", "WRITE", leave),
                permIfMissing("LEAVE_MANAGE", "Manage leave types", "WRITE", leave),
                permIfMissing("COMMUNICATION_SEND", "Send messages", "WRITE", comms),
                permIfMissing("COMMUNICATION_MANAGE", "Manage communications", "WRITE", comms),
                permIfMissing("BILLS_MANAGE", "Manage bills", "WRITE", finance),
                permIfMissing("SUPPLIERS_MANAGE", "Manage suppliers", "WRITE", finance),
                permIfMissing("SETUP_MANAGE", "Manage system setups", "WRITE", setup),
                permIfMissing("SCHOOL_SETUP", "Configure school info", "WRITE", setup),
                permIfMissing("SALARY_MANAGE", "Manage staff salaries", "WRITE", payroll),
                // Genuinely new — no Staff/Attendance/Transport permission concept existed before
                permIfMissing("STAFF_VIEW", "View staff records", "READ", admin),
                permIfMissing("STAFF_MANAGE", "Create, edit, and remove staff records", "WRITE", admin),
                permIfMissing("ATTENDANCE_VIEW", "View school-wide attendance reports", "READ", attendance),
                permIfMissing("ATTENDANCE_MANAGE", "Manage biometric devices and class-teacher assignments", "WRITE", attendance),
                permIfMissing("TRANSPORT_MANAGE", "Manage transport routes and enrollments", "WRITE", transport)
        );

        roleRepo.findByName("ADMIN").ifPresent(adminRole -> {
            boolean changed = false;
            for (Permission p : toBackfill) {
                if (adminRole.getPermissions().add(p)) changed = true;
            }
            if (changed) roleRepo.save(adminRole);

            for (User user : userRepo.findAll()) {
                if (!user.getRoles().contains(adminRole)) continue;
                for (Permission p : toBackfill) {
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
            executeSqlSection("INSERT INTO bloom_sch_paye_bands", "payeBands");
        } else {
            log.info("PAYE bands already seeded, skipping");
        }

        if (statutoryDeductionRepo.count() == 0) {
            log.info("Seeding statutory deductions (NSSF, Housing Levy, SHIF)...");
            executeSqlSection("INSERT INTO bloom_sch_statutory_deductions", "statutoryDeductions");
        } else {
            log.info("Statutory deductions already seeded, skipping");
        }

        if (payrollSettingsRepo.count() == 0) {
            log.info("Seeding payroll settings...");
            executeSqlSection("INSERT INTO bloom_sch_payroll_settings", "payrollSettings");
        } else {
            log.info("Payroll settings already seeded, skipping");
        }
    }

    /** Seeds the full Kenyan CBC grade structure (2-6-3-3): Pre-Primary, Primary, Junior School,
     *  Senior School — Grade 10 is the newest cohort, rolling out from 2026. Idempotent: only
     *  runs against an empty table, so it never overwrites a school's own grade configuration. */
    private void seedGradeLevels() {
        if (gradeLevelRepo.count() > 0) { log.info("Grade levels already seeded, skipping"); return; }
        log.info("Seeding grade levels...");
        executeSqlSection("INSERT INTO bloom_sch_grade_levels", "gradeLevels");
    }

    /** Runs the statement(s) in seed.sql whose text contains {@code keyword} — lets each section
     *  of that flat-file live as plain SQL (easy for a DBA to read/edit) while the calling method
     *  still decides *whether* to run it via its own idempotency check. Mirrors the pattern this
     *  team already uses in the LegalTech backend's SeedService. */
    private void executeSqlSection(String keyword, String label) {
        try {
            ClassPathResource resource = new ClassPathResource("seed.sql");
            String sql = new String(FileCopyUtils.copyToByteArray(resource.getInputStream()), StandardCharsets.UTF_8);

            StringBuilder sectionSql = new StringBuilder();
            String[] statements = sql.split(";");
            for (String statement : statements) {
                String trimmed = statement.trim();
                if (trimmed.contains(keyword)) {
                    sectionSql.append(trimmed).append(";");
                }
            }

            if (sectionSql.isEmpty()) {
                log.warn("No SQL section found for {}", label);
                return;
            }

            log.debug("Executing SQL section for {}:\n{}", label, sectionSql);
            jdbcTemplate.execute(sectionSql.toString());
        } catch (IOException e) {
            log.error("Failed to read seed.sql for {}", label, e);
        } catch (Exception e) {
            log.error("Failed to execute SQL section for {}", label, e);
        }
    }
}
