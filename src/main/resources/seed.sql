-- ============================================================
-- BloomSchool Seed Data
--
-- Executed automatically on every boot by SeedService.executeSqlSection() (see
-- common/utils/SeedService.java), each section guarded by its own `repository.count() == 0`
-- check so re-running never duplicates or overwrites data an admin has already configured.
--
-- Scope: this file holds simple, flat reference/lookup data only. Modules, permissions, roles,
-- users and the payroll-workflow-permission backfill stay in SeedService.java directly — that
-- logic involves conditional filtering/backfill (e.g. granting a permission only to existing
-- ADMIN role holders) that doesn't translate cleanly into plain SQL.
-- ============================================================

-- ========================================
-- PAYE Bands (2023 Finance Act rates)
-- ========================================
INSERT INTO bloom_sch_paye_bands (uuid, min_amount, max_amount, rate, display_order) VALUES
    (gen_random_uuid(), 0,      24000,  10,   0),
    (gen_random_uuid(), 24001,  32333,  25,   24001),
    (gen_random_uuid(), 32334,  500000, 30,   32334),
    (gen_random_uuid(), 500001, 800000, 32.5, 500001),
    (gen_random_uuid(), 800001, NULL,   35,   800001);

-- ========================================
-- Statutory Deductions — NSSF (Feb 2025 tier limits), Affordable Housing Levy, SHIF
-- (SHIF replaced NHIF in Oct 2024: flat 2.75% of gross with a KES 300 floor, not a tiered table)
-- ========================================
INSERT INTO bloom_sch_statutory_deductions
    (uuid, name, type, category, value, threshold_amount, max_amount, min_amount, employer_contribution, employer_value, active) VALUES
    (gen_random_uuid(), 'NSSF Tier I',             'PERCENTAGE', 'NSSF',         6,    0,    480,  NULL, true,  6,   true),
    (gen_random_uuid(), 'NSSF Tier II',            'PERCENTAGE', 'NSSF',         6,    8000, 3840, NULL, true,  6,   true),
    (gen_random_uuid(), 'Affordable Housing Levy', 'PERCENTAGE', 'HOUSING_LEVY', 1.5,  NULL, NULL, NULL, true,  1.5, true),
    (gen_random_uuid(), 'SHIF',                    'PERCENTAGE', 'SHIF',         2.75, NULL, NULL, 300,  false, 0,   true);

-- ========================================
-- Payroll Settings (personal relief, insurance relief, pay day, default method, currency)
-- ========================================
INSERT INTO bloom_sch_payroll_settings (uuid, personal_relief, insurance_relief, pay_day, payment_method, currency) VALUES
    (gen_random_uuid(), 2400, 60000, 28, 'bank_transfer', 'KES');

-- ========================================
-- Grade Levels — Kenyan CBC structure (2-6-3-3): Pre-Primary, Primary, Junior School, Senior
-- School. Grade 10 (Senior School) is the newest cohort. All seeded as single-stream (streams=1);
-- schools with multiple streams per grade name them individually via the Grade Levels screen.
-- ========================================
INSERT INTO bloom_sch_grade_levels (uuid, name, display_order, streams, status) VALUES
    (gen_random_uuid(), 'PP1',      1,  1, 'ACTIVE'),
    (gen_random_uuid(), 'PP2',      2,  1, 'ACTIVE'),
    (gen_random_uuid(), 'Grade 1',  3,  1, 'ACTIVE'),
    (gen_random_uuid(), 'Grade 2',  4,  1, 'ACTIVE'),
    (gen_random_uuid(), 'Grade 3',  5,  1, 'ACTIVE'),
    (gen_random_uuid(), 'Grade 4',  6,  1, 'ACTIVE'),
    (gen_random_uuid(), 'Grade 5',  7,  1, 'ACTIVE'),
    (gen_random_uuid(), 'Grade 6',  8,  1, 'ACTIVE'),
    (gen_random_uuid(), 'Grade 7',  9,  1, 'ACTIVE'),
    (gen_random_uuid(), 'Grade 8',  10, 1, 'ACTIVE'),
    (gen_random_uuid(), 'Grade 9',  11, 1, 'ACTIVE'),
    (gen_random_uuid(), 'Grade 10', 12, 1, 'ACTIVE'),
    (gen_random_uuid(), 'Grade 11', 13, 1, 'ACTIVE'),
    (gen_random_uuid(), 'Grade 12', 14, 1, 'ACTIVE');
