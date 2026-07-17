-- ============================================================
-- BloomSchool Seed Data
-- Run manually: psql -U postgres -d bloomschool -f seed.sql
-- Or let AuthService.seedDemoAccounts() handle it via JPA
-- ============================================================

-- ========================================
-- 1. MODULES
-- ========================================
INSERT INTO bloom_sys_module (id, module_name, uuid) VALUES
    (1,  'Dashboard Module',   gen_random_uuid()),
    (2,  'Admin Module',       gen_random_uuid()),
    (3,  'Students Module',    gen_random_uuid()),
    (4,  'Academics Module',   gen_random_uuid()),
    (5,  'Finance Module',     gen_random_uuid()),
    (6,  'Payroll Module',     gen_random_uuid()),
    (7,  'Leave Module',       gen_random_uuid()),
    (8,  'Reports Module',     gen_random_uuid()),
    (9,  'Communication Module', gen_random_uuid()),
    (10, 'System Setup Module', gen_random_uuid())
ON CONFLICT DO NOTHING;

-- ========================================
-- 2. PERMISSIONS
-- ========================================

-- Dashboard Module (1)
INSERT INTO bloom_sys_permission (name, perm_desc, access_type, sys_module_id, uuid) VALUES
    ('DASHBOARD_VIEW',     'View dashboard',          'READ',   1, gen_random_uuid()),
    ('DASHBOARD_VIEW_ALL', 'View full admin dashboard','READ',  1, gen_random_uuid())
ON CONFLICT (name) DO NOTHING;

-- Admin Module (2)
INSERT INTO bloom_sys_permission (name, perm_desc, access_type, sys_module_id, uuid) VALUES
    ('USER_VIEW',        'View users',           'READ',   2, gen_random_uuid()),
    ('USER_CREATE',      'Create users',         'WRITE',  2, gen_random_uuid()),
    ('USER_EDIT',        'Edit users',           'WRITE',  2, gen_random_uuid()),
    ('USER_DELETE',      'Delete users',         'DELETE', 2, gen_random_uuid()),
    ('ROLE_VIEW',        'View roles',           'READ',   2, gen_random_uuid()),
    ('ROLE_CREATE',      'Create roles',         'WRITE',  2, gen_random_uuid()),
    ('ROLE_EDIT',        'Edit roles',           'WRITE',  2, gen_random_uuid()),
    ('ROLE_DELETE',      'Delete roles',         'DELETE', 2, gen_random_uuid()),
    ('ROLE_ASSIGN',      'Assign roles to users','WRITE',  2, gen_random_uuid()),
    ('PERMISSION_ASSIGN','Assign permissions',   'WRITE',  2, gen_random_uuid())
ON CONFLICT (name) DO NOTHING;

-- Students Module (3)
INSERT INTO bloom_sys_permission (name, perm_desc, access_type, sys_module_id, uuid) VALUES
    ('STUDENT_VIEW',     'View students',        'READ',   3, gen_random_uuid()),
    ('STUDENT_CREATE',   'Admit students',       'WRITE',  3, gen_random_uuid()),
    ('STUDENT_EDIT',     'Edit student records', 'WRITE',  3, gen_random_uuid()),
    ('STUDENT_DELETE',   'Delete students',      'DELETE', 3, gen_random_uuid()),
    ('ADMISSION_VIEW',   'View admissions',      'READ',   3, gen_random_uuid()),
    ('ADMISSION_MANAGE', 'Manage admissions',    'WRITE',  3, gen_random_uuid())
ON CONFLICT (name) DO NOTHING;

-- Academics Module (4)
INSERT INTO bloom_sys_permission (name, perm_desc, access_type, sys_module_id, uuid) VALUES
    ('ACADEMICS_VIEW',   'View academics',       'READ',   4, gen_random_uuid()),
    ('GRADES_VIEW',      'View grades',          'READ',   4, gen_random_uuid()),
    ('GRADES_ENTER',     'Enter/edit grades',    'WRITE',  4, gen_random_uuid()),
    ('TIMETABLE_VIEW',   'View timetable',       'READ',   4, gen_random_uuid()),
    ('TIMETABLE_MANAGE', 'Manage timetable',     'WRITE',  4, gen_random_uuid()),
    ('SUBJECTS_MANAGE',  'Manage subjects',      'WRITE',  4, gen_random_uuid()),
    ('REPORTS_VIEW',     'View term reports',    'READ',   4, gen_random_uuid())
ON CONFLICT (name) DO NOTHING;

-- Finance Module (5)
INSERT INTO bloom_sys_permission (name, perm_desc, access_type, sys_module_id, uuid) VALUES
    ('FEES_VIEW',        'View fee records',     'READ',   5, gen_random_uuid()),
    ('FEES_MANAGE',      'Manage fee structure', 'WRITE',  5, gen_random_uuid()),
    ('FEES_COLLECT',     'Record fee payments',  'WRITE',  5, gen_random_uuid()),
    ('FINANCE_VIEW',     'View finance module',  'READ',   5, gen_random_uuid()),
    ('FINANCE_MANAGE',   'Manage finance',       'WRITE',  5, gen_random_uuid()),
    ('SUPPLIERS_MANAGE', 'Manage suppliers',     'WRITE',  5, gen_random_uuid()),
    ('BILLS_MANAGE',     'Manage bills',         'WRITE',  5, gen_random_uuid())
ON CONFLICT (name) DO NOTHING;

-- Payroll Module (6)
INSERT INTO bloom_sys_permission (name, perm_desc, access_type, sys_module_id, uuid) VALUES
    ('PAYROLL_VIEW',     'View payroll',         'READ',   6, gen_random_uuid()),
    ('PAYROLL_RUN',      'Run payroll',          'WRITE',  6, gen_random_uuid()),
    ('PAYSLIP_VIEW',     'View own payslip',     'READ',   6, gen_random_uuid()),
    ('SALARY_MANAGE',    'Manage staff salaries','WRITE',  6, gen_random_uuid())
ON CONFLICT (name) DO NOTHING;

-- Leave Module (7)
INSERT INTO bloom_sys_permission (name, perm_desc, access_type, sys_module_id, uuid) VALUES
    ('LEAVE_VIEW',       'View leave requests',  'READ',   7, gen_random_uuid()),
    ('LEAVE_APPLY',      'Apply for leave',      'WRITE',  7, gen_random_uuid()),
    ('LEAVE_APPROVE',    'Approve leave',        'WRITE',  7, gen_random_uuid()),
    ('LEAVE_MANAGE',     'Manage leave types',   'WRITE',  7, gen_random_uuid())
ON CONFLICT (name) DO NOTHING;

-- Reports Module (8)
INSERT INTO bloom_sys_permission (name, perm_desc, access_type, sys_module_id, uuid) VALUES
    ('REPORT_VIEW',      'View reports',         'READ',   8, gen_random_uuid()),
    ('REPORT_EXPORT',    'Export reports',       'READ',   8, gen_random_uuid()),
    ('REPORT_GENERATE',  'Generate reports',     'WRITE',  8, gen_random_uuid())
ON CONFLICT (name) DO NOTHING;

-- Communication Module (9)
INSERT INTO bloom_sys_permission (name, perm_desc, access_type, sys_module_id, uuid) VALUES
    ('COMMUNICATION_VIEW',   'View messages',    'READ',   9, gen_random_uuid()),
    ('COMMUNICATION_SEND',   'Send messages',    'WRITE',  9, gen_random_uuid()),
    ('COMMUNICATION_MANAGE', 'Manage comms',     'WRITE',  9, gen_random_uuid())
ON CONFLICT (name) DO NOTHING;

-- System Setup Module (10)
INSERT INTO bloom_sys_permission (name, perm_desc, access_type, sys_module_id, uuid) VALUES
    ('SETUP_VIEW',       'View system setups',   'READ',   10, gen_random_uuid()),
    ('SETUP_MANAGE',     'Manage system setups', 'WRITE',  10, gen_random_uuid()),
    ('SCHOOL_SETUP',     'Configure school info','WRITE',  10, gen_random_uuid())
ON CONFLICT (name) DO NOTHING;

-- ========================================
-- 3. ROLES
-- ========================================
INSERT INTO bloom_sys_role (id, name, uuid) VALUES
    (1, 'ADMIN',   gen_random_uuid()),
    (2, 'TEACHER', gen_random_uuid()),
    (3, 'PARENT',  gen_random_uuid())
ON CONFLICT DO NOTHING;

-- ========================================
-- 4. ROLE → PERMISSION ASSIGNMENTS
-- ========================================

-- ADMIN gets ALL permissions
INSERT INTO bloom_sys_role_permissions (role_id, permission_id)
SELECT 1, p.id FROM bloom_sys_permission p
ON CONFLICT DO NOTHING;

-- TEACHER permissions
INSERT INTO bloom_sys_role_permissions (role_id, permission_id)
SELECT 2, p.id FROM bloom_sys_permission p
WHERE p.name IN (
    'DASHBOARD_VIEW',
    'GRADES_VIEW', 'GRADES_ENTER', 'ACADEMICS_VIEW',
    'TIMETABLE_VIEW', 'REPORTS_VIEW',
    'LEAVE_VIEW', 'LEAVE_APPLY',
    'PAYSLIP_VIEW',
    'COMMUNICATION_VIEW', 'COMMUNICATION_SEND'
)
ON CONFLICT DO NOTHING;

-- PARENT permissions
INSERT INTO bloom_sys_role_permissions (role_id, permission_id)
SELECT 3, p.id FROM bloom_sys_permission p
WHERE p.name IN (
    'DASHBOARD_VIEW',
    'GRADES_VIEW', 'REPORTS_VIEW',
    'FEES_VIEW',
    'COMMUNICATION_VIEW', 'COMMUNICATION_SEND'
)
ON CONFLICT DO NOTHING;

-- ========================================
-- 5. USERS  (password = BCrypt of literal)
--    admin   → admin123
--    teacher → teacher123
--    parent  → parent123
-- ========================================
INSERT INTO bloom_sys_users
    (uuid, user_name, first_name, other_names, email, phone_number,
     password, active, first_login, enable2_f_a)
VALUES
    (gen_random_uuid(), 'admin',   'School',  'Admin',
     'admin@bloomschool.com',   '0700000001',
     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh7y',
     true, false, false),
    (gen_random_uuid(), 'teacher', 'Demo',    'Teacher',
     'teacher@bloomschool.com', '0700000002',
     '$2a$10$TbBPMFMFMFMFMFMFMFMFMOQKQKQKQKQKQKQKQKQKQKQKQKQKQKQKa',
     true, false, false),
    (gen_random_uuid(), 'parent',  'Demo',    'Parent',
     'parent@bloomschool.com',  '0700000003',
     '$2a$10$TbBPMFMFMFMFMFMFMFMFMOQKQKQKQKQKQKQKQKQKQKQKQKQKQKQKa',
     true, false, false)
ON CONFLICT (user_name) DO NOTHING;

-- ========================================
-- 6. USER → ROLE ASSIGNMENTS
-- ========================================
INSERT INTO bloom_sys_user_roles (user_id, role_id)
SELECT u.id, 1 FROM bloom_sys_users u WHERE u.user_name = 'admin'
ON CONFLICT DO NOTHING;

INSERT INTO bloom_sys_user_roles (user_id, role_id)
SELECT u.id, 2 FROM bloom_sys_users u WHERE u.user_name = 'teacher'
ON CONFLICT DO NOTHING;

INSERT INTO bloom_sys_user_roles (user_id, role_id)
SELECT u.id, 3 FROM bloom_sys_users u WHERE u.user_name = 'parent'
ON CONFLICT DO NOTHING;

-- ========================================
-- 7. USER PERMISSIONS (INHERITED from roles)
-- ========================================
INSERT INTO bloom_sys_user_permissions (user_id, permission_id, override_type)
SELECT u.id, rp.permission_id, NULL
FROM bloom_sys_users u
JOIN bloom_sys_user_roles ur ON ur.user_id = u.id
JOIN bloom_sys_role_permissions rp ON rp.role_id = ur.role_id
ON CONFLICT DO NOTHING;
