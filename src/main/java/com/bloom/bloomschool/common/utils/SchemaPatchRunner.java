package com.bloom.bloomschool.common.utils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Runs schema fixes Hibernate's {@code ddl-auto: update} can't express from JPA annotations alone
 * (partial/conditional unique indexes, or a CHECK constraint that needs regenerating after adding
 * an enum value) — this project has no Flyway/Liquibase, so these run as idempotent native SQL on
 * every boot instead. Safe to re-run: every statement is a no-op if the schema already matches.
 */
@Component
@Slf4j
@Order(0) // must run before DataSeeder (@Order(1)) — seeding a SHIF row needs this constraint fixed first
public class SchemaPatchRunner implements CommandLineRunner {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Applying schema patches...");

        // Only one non-rejected payroll run may exist per year+month — the app already checks this
        // in PayrollService.processPayroll(), but a stale/racing client shouldn't be able to slip a
        // duplicate past that check, so it's enforced at the database level too.
        entityManager.createNativeQuery(
                "CREATE UNIQUE INDEX IF NOT EXISTS ux_payroll_runs_active_month " +
                "ON bloom_sch_payroll_runs (year, month_index) WHERE status <> 'REJECTED'"
        ).executeUpdate();

        // Hibernate's ddl-auto:update generates a CHECK constraint from the Category enum only when
        // a table is first created — it doesn't widen an existing constraint when a value (SHIF) is
        // added later, so a DB created before that change would reject SHIF rows. Recreate it fresh
        // on every boot so this holds regardless of when the table was originally created.
        entityManager.createNativeQuery(
                "ALTER TABLE bloom_sch_statutory_deductions " +
                "DROP CONSTRAINT IF EXISTS bloom_sch_statutory_deductions_category_check"
        ).executeUpdate();
        entityManager.createNativeQuery(
                "ALTER TABLE bloom_sch_statutory_deductions " +
                "ADD CONSTRAINT bloom_sch_statutory_deductions_category_check " +
                "CHECK (category IN ('NSSF','HOUSING_LEVY','SHIF','OTHER'))"
        ).executeUpdate();

        // StudentBioData/StaffBioData moved from plain-text template ref strings to real,
        // encrypted SourceAFIS templates (see AesGcmByteConverter) stored under new column
        // names — Hibernate's ddl-auto:update creates the new bytea columns fine, but never
        // drops the old ones, so remove the orphaned columns explicitly here.
        entityManager.createNativeQuery(
                "ALTER TABLE bloom_sch_student_bio_data " +
                "DROP COLUMN IF EXISTS left_fingerprint_template_ref, " +
                "DROP COLUMN IF EXISTS right_fingerprint_template_ref"
        ).executeUpdate();
        entityManager.createNativeQuery(
                "ALTER TABLE bloom_sch_staff_bio_data " +
                "DROP COLUMN IF EXISTS left_fingerprint_template_ref, " +
                "DROP COLUMN IF EXISTS right_fingerprint_template_ref"
        ).executeUpdate();

        log.info("Schema patches applied");
    }
}
