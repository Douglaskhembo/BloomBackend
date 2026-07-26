package com.bloom.bloomschool.payroll.service;

import com.bloom.bloomschool.auth.model.Permission;
import com.bloom.bloomschool.auth.model.User;
import com.bloom.bloomschool.auth.model.UserPermission;
import com.bloom.bloomschool.auth.repo.PermissionRepository;
import com.bloom.bloomschool.auth.repo.UserPermissionRepository;
import com.bloom.bloomschool.auth.repo.UserRepository;
import com.bloom.bloomschool.common.utils.UserUtils;
import com.bloom.bloomschool.payroll.dto.*;
import com.bloom.bloomschool.payroll.entity.*;
import com.bloom.bloomschool.payroll.repository.*;
import com.bloom.bloomschool.staff.dto.StaffPaymentDetailsRequest;
import com.bloom.bloomschool.staff.entity.Staff;
import com.bloom.bloomschool.staff.entity.StaffPaymentDetails;
import com.bloom.bloomschool.staff.repository.StaffPaymentDetailsRepository;
import com.bloom.bloomschool.staff.repository.StaffRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PayrollService {

    private final PayeBandRepository payeBandRepo;
    private final NhifTierRepository nhifTierRepo;
    private final AllowanceTypeRepository allowanceTypeRepo;
    private final OtherDeductionRepository otherDeductionRepo;
    private final StatutoryDeductionRepository statutoryDeductionRepo;
    private final PayrollSettingsRepository settingsRepo;
    private final StaffSalaryRepository staffSalaryRepo;
    private final PayrollRunRepository payrollRunRepo;
    private final KenyaPayrollEngine engine;

    private final BankRepository bankRepo;
    private final MobileMoneyProviderRepository mobileMoneyProviderRepo;
    private final PaymentTypeRepository paymentTypeRepo;
    private final StaffPaymentDetailsRepository staffPaymentDetailsRepo;
    private final PayrollWorkflowStepRepository workflowStepRepo;
    private final PayrollRunApprovalRepository approvalRepo;
    private final PayrollMakerRepository payrollMakerRepo;
    private final StaffRepository staffRepo;
    private final UserRepository userRepo;
    private final UserUtils userUtils;
    private final PermissionRepository permissionRepo;
    private final UserPermissionRepository userPermissionRepo;

    // ── PAYE Bands ────────────────────────────────────────────────────────────

    public List<PayeBand> getAllPayeBands() {
        return payeBandRepo.findAllByOrderByDisplayOrderAsc();
    }

    @Transactional
    public PayeBand createPayeBand(PayeBandRequest req) {
        return payeBandRepo.save(PayeBand.builder()
                .minAmount(req.getMinAmount())
                .maxAmount(req.getMaxAmount())
                .rate(req.getRate())
                .displayOrder(req.getDisplayOrder())
                .build());
    }

    @Transactional
    public PayeBand updatePayeBand(Long id, PayeBandRequest req) {
        PayeBand b = payeBandRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("PAYE band not found"));
        b.setMinAmount(req.getMinAmount());
        b.setMaxAmount(req.getMaxAmount());
        b.setRate(req.getRate());
        b.setDisplayOrder(req.getDisplayOrder());
        return payeBandRepo.save(b);
    }

    @Transactional
    public void deletePayeBand(Long id) {
        payeBandRepo.deleteById(id);
    }

    // ── NHIF Tiers ────────────────────────────────────────────────────────────

    public List<NhifTier> getAllNhifTiers() {
        return nhifTierRepo.findAllByOrderByDisplayOrderAsc();
    }

    @Transactional
    public NhifTier createNhifTier(NhifTierRequest req) {
        return nhifTierRepo.save(NhifTier.builder()
                .minSalary(req.getMinSalary())
                .maxSalary(req.getMaxSalary())
                .amount(req.getAmount())
                .displayOrder(req.getDisplayOrder())
                .build());
    }

    @Transactional
    public NhifTier updateNhifTier(Long id, NhifTierRequest req) {
        NhifTier t = nhifTierRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("NHIF tier not found"));
        t.setMinSalary(req.getMinSalary());
        t.setMaxSalary(req.getMaxSalary());
        t.setAmount(req.getAmount());
        t.setDisplayOrder(req.getDisplayOrder());
        return nhifTierRepo.save(t);
    }

    @Transactional
    public void deleteNhifTier(Long id) {
        nhifTierRepo.deleteById(id);
    }

    // ── Allowance Types ───────────────────────────────────────────────────────

    public List<AllowanceType> getAllAllowanceTypes() {
        return allowanceTypeRepo.findAll();
    }

    @Transactional
    public AllowanceType createAllowanceType(AllowanceTypeRequest req) {
        if (allowanceTypeRepo.existsByName(req.getName()))
            throw new IllegalArgumentException("Allowance type '" + req.getName() + "' already exists");
        return allowanceTypeRepo.save(AllowanceType.builder()
                .name(req.getName())
                .type(req.getType())
                .defaultValue(req.getDefaultValue())
                .taxable(req.isTaxable())
                .build());
    }

    @Transactional
    public AllowanceType updateAllowanceType(Long id, AllowanceTypeRequest req) {
        AllowanceType a = allowanceTypeRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Allowance type not found"));
        a.setName(req.getName());
        a.setType(req.getType());
        a.setDefaultValue(req.getDefaultValue());
        a.setTaxable(req.isTaxable());
        return allowanceTypeRepo.save(a);
    }

    @Transactional
    public AllowanceType toggleAllowanceType(Long id) {
        AllowanceType a = allowanceTypeRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Allowance type not found"));
        a.setActive(!a.isActive());
        return allowanceTypeRepo.save(a);
    }

    @Transactional
    public void deleteAllowanceType(Long id) {
        allowanceTypeRepo.deleteById(id);
    }

    // ── Other Deductions ──────────────────────────────────────────────────────

    public List<OtherDeduction> getAllOtherDeductions() {
        return otherDeductionRepo.findAll();
    }

    @Transactional
    public OtherDeduction createOtherDeduction(OtherDeductionRequest req) {
        if (otherDeductionRepo.existsByName(req.getName()))
            throw new IllegalArgumentException("Deduction '" + req.getName() + "' already exists");
        return otherDeductionRepo.save(OtherDeduction.builder()
                .name(req.getName())
                .type(req.getType())
                .defaultValue(req.getDefaultValue())
                .mandatory(req.isMandatory())
                .build());
    }

    @Transactional
    public OtherDeduction updateOtherDeduction(Long id, OtherDeductionRequest req) {
        OtherDeduction d = otherDeductionRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Deduction not found"));
        d.setName(req.getName());
        d.setType(req.getType());
        d.setDefaultValue(req.getDefaultValue());
        d.setMandatory(req.isMandatory());
        return otherDeductionRepo.save(d);
    }

    @Transactional
    public OtherDeduction toggleOtherDeduction(Long id) {
        OtherDeduction d = otherDeductionRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Deduction not found"));
        d.setActive(!d.isActive());
        return otherDeductionRepo.save(d);
    }

    @Transactional
    public void deleteOtherDeduction(Long id) {
        otherDeductionRepo.deleteById(id);
    }

    // ── Statutory Deductions (NSSF, Housing Levy, custom) ────────────────────────

    public List<StatutoryDeduction> getAllStatutoryDeductions() {
        return statutoryDeductionRepo.findAll();
    }

    @Transactional
    public StatutoryDeduction createStatutoryDeduction(StatutoryDeductionRequest req) {
        if (statutoryDeductionRepo.existsByName(req.getName()))
            throw new IllegalArgumentException("Statutory deduction '" + req.getName() + "' already exists");
        return statutoryDeductionRepo.save(StatutoryDeduction.builder()
                .name(req.getName())
                .type(req.getType())
                .category(req.getCategory())
                .value(req.getValue())
                .maxAmount(req.getMaxAmount())
                .minAmount(req.getMinAmount())
                .thresholdAmount(req.getThresholdAmount())
                .employerContribution(req.isEmployerContribution())
                .employerValue(req.getEmployerValue())
                .build());
    }

    @Transactional
    public StatutoryDeduction updateStatutoryDeduction(Long id, StatutoryDeductionRequest req) {
        StatutoryDeduction d = statutoryDeductionRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Statutory deduction not found"));
        d.setName(req.getName());
        d.setType(req.getType());
        d.setCategory(req.getCategory());
        d.setValue(req.getValue());
        d.setMaxAmount(req.getMaxAmount());
        d.setMinAmount(req.getMinAmount());
        d.setThresholdAmount(req.getThresholdAmount());
        d.setEmployerContribution(req.isEmployerContribution());
        d.setEmployerValue(req.getEmployerValue());
        return statutoryDeductionRepo.save(d);
    }

    @Transactional
    public StatutoryDeduction toggleStatutoryDeduction(Long id) {
        StatutoryDeduction d = statutoryDeductionRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Statutory deduction not found"));
        d.setActive(!d.isActive());
        return statutoryDeductionRepo.save(d);
    }

    @Transactional
    public void deleteStatutoryDeduction(Long id) {
        statutoryDeductionRepo.deleteById(id);
    }

    // ── Payroll Settings ──────────────────────────────────────────────────────

    public PayrollSettings getSettings() {
        return settingsRepo.findAll().stream().findFirst()
                .orElse(defaultSettings());
    }

    @Transactional
    public PayrollSettings saveSettings(PayrollSettingsRequest req) {
        PayrollSettings s = settingsRepo.findAll().stream().findFirst()
                .orElse(new PayrollSettings());
        s.setPersonalRelief(req.getPersonalRelief());
        s.setInsuranceRelief(req.getInsuranceRelief());
        s.setPayDay(req.getPayDay());
        s.setPaymentMethod(req.getPaymentMethod());
        s.setCurrency(req.getCurrency() != null ? req.getCurrency() : "KES");
        return settingsRepo.save(s);
    }

    // ── Staff Salaries ────────────────────────────────────────────────────────

    public List<StaffSalary> getAllStaffSalaries() {
        return staffSalaryRepo.findAll();
    }

    public StaffSalary getStaffSalary(String staffId) {
        return staffSalaryRepo.findByStaffId(staffId)
                .orElseThrow(() -> new EntityNotFoundException("Salary not configured for staff: " + staffId));
    }

    @Transactional
    public StaffSalary saveStaffSalary(StaffSalaryRequest req) {
        StaffSalary s = staffSalaryRepo.findByStaffId(req.getStaffId())
                .orElse(StaffSalary.builder().staffId(req.getStaffId()).build());
        s.setBasicSalary(req.getBasicSalary());
        if (req.getAllowances() != null) s.setAllowances(req.getAllowances());
        if (req.getDeductions() != null) s.setDeductions(req.getDeductions());
        return staffSalaryRepo.save(s);
    }

    @Transactional
    public void deleteStaffSalary(String staffId) {
        staffSalaryRepo.findByStaffId(staffId).ifPresent(staffSalaryRepo::delete);
    }

    // ── Banks ─────────────────────────────────────────────────────────────────

    public List<Bank> getAllBanks() {
        return bankRepo.findAll();
    }

    @Transactional
    public Bank createBank(BankRequest req) {
        if (bankRepo.existsByName(req.getName()))
            throw new IllegalArgumentException("Bank '" + req.getName() + "' already exists");
        return bankRepo.save(Bank.builder()
                .name(req.getName()).bankCode(req.getBankCode()).swiftCode(req.getSwiftCode())
                .build());
    }

    @Transactional
    public Bank updateBank(Long id, BankRequest req) {
        Bank b = bankRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Bank not found"));
        b.setName(req.getName());
        b.setBankCode(req.getBankCode());
        b.setSwiftCode(req.getSwiftCode());
        return bankRepo.save(b);
    }

    @Transactional
    public Bank toggleBank(Long id) {
        Bank b = bankRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Bank not found"));
        b.setActive(!b.isActive());
        return bankRepo.save(b);
    }

    @Transactional
    public void deleteBank(Long id) {
        bankRepo.deleteById(id);
    }

    // ── Mobile Money Providers ────────────────────────────────────────────────

    public List<MobileMoneyProvider> getAllMobileMoneyProviders() {
        return mobileMoneyProviderRepo.findAll();
    }

    @Transactional
    public MobileMoneyProvider createMobileMoneyProvider(MobileMoneyProviderRequest req) {
        if (mobileMoneyProviderRepo.existsByName(req.getName()))
            throw new IllegalArgumentException("Provider '" + req.getName() + "' already exists");
        return mobileMoneyProviderRepo.save(MobileMoneyProvider.builder()
                .name(req.getName()).shortCode(req.getShortCode())
                .build());
    }

    @Transactional
    public MobileMoneyProvider updateMobileMoneyProvider(Long id, MobileMoneyProviderRequest req) {
        MobileMoneyProvider p = mobileMoneyProviderRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Provider not found"));
        p.setName(req.getName());
        p.setShortCode(req.getShortCode());
        return mobileMoneyProviderRepo.save(p);
    }

    @Transactional
    public MobileMoneyProvider toggleMobileMoneyProvider(Long id) {
        MobileMoneyProvider p = mobileMoneyProviderRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Provider not found"));
        p.setActive(!p.isActive());
        return mobileMoneyProviderRepo.save(p);
    }

    @Transactional
    public void deleteMobileMoneyProvider(Long id) {
        mobileMoneyProviderRepo.deleteById(id);
    }

    // ── Payment Types (settlement rail for the bank-submission export) ─────────

    public List<PaymentType> getAllPaymentTypes() {
        return paymentTypeRepo.findAll();
    }

    @Transactional
    public PaymentType createPaymentType(PaymentTypeRequest req) {
        String code = req.getCode().trim().toUpperCase();
        if (paymentTypeRepo.existsByCode(code))
            throw new IllegalArgumentException("Payment type '" + code + "' already exists");
        return paymentTypeRepo.save(PaymentType.builder()
                .category(req.getCategory()).code(code)
                .build());
    }

    @Transactional
    public PaymentType updatePaymentType(Long id, PaymentTypeRequest req) {
        PaymentType p = paymentTypeRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Payment type not found"));
        p.setCategory(req.getCategory());
        p.setCode(req.getCode().trim().toUpperCase());
        return paymentTypeRepo.save(p);
    }

    @Transactional
    public PaymentType togglePaymentType(Long id) {
        PaymentType p = paymentTypeRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Payment type not found"));
        p.setActive(!p.isActive());
        return paymentTypeRepo.save(p);
    }

    @Transactional
    public void deletePaymentType(Long id) {
        paymentTypeRepo.deleteById(id);
    }

    // ── Staff Payment Details (Finance-owned bank/mobile-money info) ────────────

    public List<StaffPaymentDetails> getAllStaffPaymentDetails() {
        return staffPaymentDetailsRepo.findAll();
    }

    public Optional<StaffPaymentDetails> getStaffPaymentDetails(String staffId) {
        return staffPaymentDetailsRepo.findByStaffId(staffId);
    }

    @Transactional
    public StaffPaymentDetails saveStaffPaymentDetails(StaffPaymentDetailsRequest req) {
        StaffPaymentDetails d = staffPaymentDetailsRepo.findByStaffId(req.getStaffId())
                .orElse(StaffPaymentDetails.builder().staffId(req.getStaffId()).build());
        d.setPaymentMethod(req.getPaymentMethod());
        if (req.getBankUuid() != null) {
            d.setBank(bankRepo.findAll().stream().filter(b -> b.getUuid().equals(req.getBankUuid())).findFirst()
                    .orElseThrow(() -> new EntityNotFoundException("Bank not found")));
        } else {
            d.setBank(null);
        }
        d.setBankAccountNumber(req.getBankAccountNumber());
        d.setBankAccountName(req.getBankAccountName());
        d.setBankBranch(req.getBankBranch());
        if (req.getMobileMoneyProviderUuid() != null) {
            d.setMobileMoneyProvider(mobileMoneyProviderRepo.findAll().stream()
                    .filter(p -> p.getUuid().equals(req.getMobileMoneyProviderUuid())).findFirst()
                    .orElseThrow(() -> new EntityNotFoundException("Mobile money provider not found")));
        } else {
            d.setMobileMoneyProvider(null);
        }
        d.setMobileNumber(req.getMobileNumber());
        d.setMobileAccountName(req.getMobileAccountName());
        if (req.getPaymentTypeUuid() != null) {
            d.setPaymentType(paymentTypeRepo.findByUuid(req.getPaymentTypeUuid())
                    .orElseThrow(() -> new EntityNotFoundException("Payment type not found")));
        } else {
            d.setPaymentType(null);
        }
        return staffPaymentDetailsRepo.save(d);
    }

    // ── Payroll Approval Workflow Setup ──────────────────────────────────────────

    public List<PayrollWorkflowStep> getWorkflowSteps() {
        return workflowStepRepo.findAllByOrderBySequenceOrderAsc();
    }

    @Transactional
    public PayrollWorkflowStep createWorkflowStep(PayrollWorkflowStepRequest req) {
        int nextOrder = workflowStepRepo.findAllByOrderBySequenceOrderAsc().stream()
                .mapToInt(PayrollWorkflowStep::getSequenceOrder).max().orElse(0) + 1;
        Set<User> assignees = resolveUsers(req.getAssignedUserUuids());
        assignees.forEach(u -> grantIfMissing(u, "PAYROLL_APPROVE"));
        return workflowStepRepo.save(PayrollWorkflowStep.builder()
                .sequenceOrder(nextOrder)
                .label(req.getLabel())
                .approvalRule(req.getApprovalRule())
                .minApprovals(validateMinApprovals(req, assignees))
                .active(req.isActive())
                .assignedUsers(assignees)
                .thresholdAmount(req.getThresholdAmount())
                .build());
    }

    @Transactional
    public PayrollWorkflowStep updateWorkflowStep(UUID uuid, PayrollWorkflowStepRequest req) {
        PayrollWorkflowStep step = workflowStepRepo.findByUuid(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Workflow step not found"));
        Set<User> assignees = resolveUsers(req.getAssignedUserUuids());
        assignees.forEach(u -> grantIfMissing(u, "PAYROLL_APPROVE"));
        step.setLabel(req.getLabel());
        step.setApprovalRule(req.getApprovalRule());
        step.setMinApprovals(validateMinApprovals(req, assignees));
        step.setActive(req.isActive());
        step.setAssignedUsers(assignees);
        step.setThresholdAmount(req.getThresholdAmount());
        return workflowStepRepo.save(step);
    }

    private Integer validateMinApprovals(PayrollWorkflowStepRequest req, Set<User> assignees) {
        if (req.getApprovalRule() != PayrollWorkflowStep.ApprovalRule.AT_LEAST) return null;
        Integer min = req.getMinApprovals();
        if (min == null || min < 1)
            throw new IllegalArgumentException("Enter how many approvers are required for an 'at least N' step");
        if (min > assignees.size())
            throw new IllegalArgumentException("'At least " + min + "' can't exceed the " + assignees.size() + " assigned people");
        return min;
    }

    @Transactional
    public void deleteWorkflowStep(UUID uuid) {
        PayrollWorkflowStep step = workflowStepRepo.findByUuid(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Workflow step not found"));
        workflowStepRepo.delete(step);
    }

    @Transactional
    public List<PayrollWorkflowStep> reorderWorkflowSteps(WorkflowStepReorderRequest req) {
        List<PayrollWorkflowStep> steps = workflowStepRepo.findAllById(
                req.getStepUuids().stream().map(u -> workflowStepRepo.findByUuid(u)
                        .orElseThrow(() -> new EntityNotFoundException("Workflow step not found: " + u)).getId())
                        .collect(Collectors.toList()));
        Map<UUID, PayrollWorkflowStep> byUuid = steps.stream()
                .collect(Collectors.toMap(PayrollWorkflowStep::getUuid, s -> s));
        int order = 1;
        for (UUID uuid : req.getStepUuids()) {
            PayrollWorkflowStep step = byUuid.get(uuid);
            step.setSequenceOrder(order++);
        }
        return workflowStepRepo.saveAll(steps);
    }

    private Set<User> resolveUsers(Set<UUID> uuids) {
        if (uuids == null || uuids.isEmpty()) return new java.util.HashSet<>();
        return uuids.stream()
                .map(u -> userRepo.findByUuid(u).orElseThrow(() -> new EntityNotFoundException("User not found: " + u)))
                .collect(Collectors.toSet());
    }

    private double runTotal(PayrollRun run) {
        return run.getLines().stream().mapToDouble(PayrollLine::getNetSalary).sum();
    }

    /** Active steps that apply to this run given its size — steps with a thresholdAmount only kick in
     *  once the run's total net pay reaches that amount, so larger payrolls require more sign-off. */
    private List<PayrollWorkflowStep> applicableSteps(PayrollRun run) {
        double total = runTotal(run);
        return workflowStepRepo.findAllByActiveTrueOrderBySequenceOrderAsc().stream()
                .filter(s -> s.getThresholdAmount() == null || s.getThresholdAmount() <= 0 || total >= s.getThresholdAmount())
                .collect(Collectors.toList());
    }

    /** Assigning someone here is what grants the ability — not a prerequisite done elsewhere first. */
    private void grantIfMissing(User user, String permissionName) {
        Permission permission = permissionRepo.findByName(permissionName)
                .orElseThrow(() -> new IllegalStateException("Permission not seeded: " + permissionName));
        if (userPermissionRepo.findByUserIdAndPermissionId(user.getId(), permission.getId()) != null) return;
        userPermissionRepo.save(UserPermission.builder().user(user).permission(permission).overrideType("GRANT").build());
    }

    // ── Payroll Makers (who can generate/submit payroll) ─────────────────────────

    public List<PayrollMaker> getMakers() {
        return payrollMakerRepo.findAll();
    }

    @Transactional
    public PayrollMaker addMaker(PayrollMakerRequest req) {
        if (payrollMakerRepo.existsByUserUuid(req.getUserUuid()))
            throw new IllegalArgumentException("This person is already a maker");
        User user = userRepo.findByUuid(req.getUserUuid())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        grantIfMissing(user, "PAYROLL_RUN");
        return payrollMakerRepo.save(PayrollMaker.builder().user(user).build());
    }

    @Transactional
    public void removeMaker(UUID uuid) {
        PayrollMaker maker = payrollMakerRepo.findByUuid(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Maker not found"));
        payrollMakerRepo.delete(maker);
    }

    private void requireMaker(User actor) {
        if (!payrollMakerRepo.existsByUserId(actor.getId()))
            throw new IllegalStateException(
                    "You are not set up as a payroll maker — add yourself under Payroll Setup → Approval Workflow first");
    }

    // ── Payroll Runs ──────────────────────────────────────────────────────────

    public List<PayrollRun> getAllRuns() {
        return payrollRunRepo.findAllByOrderByProcessedAtDesc();
    }

    public PayrollRun getRun(Long id) {
        return payrollRunRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payroll run not found"));
    }

    public List<PayrollRunApproval> getRunApprovals(Long runId) {
        return approvalRepo.findByPayrollRunIdOrderByActedAtAsc(runId);
    }

    /** Any staff member's own payslip history — resolved off their own User.profileRef (their Staff.uuid),
     *  never a client-supplied id, and never the full PayrollRun (which holds everyone else's pay too). */
    public List<MyPayslipResponse> getMyPayslips() {
        User actor = currentUser();
        String staffId = actor.getProfileRef();
        if (staffId == null || staffId.isBlank()) return List.of();

        return payrollRunRepo.findAllByOrderByProcessedAtDesc().stream()
                .filter(r -> r.getStatus() == PayrollRun.Status.APPROVED || r.getStatus() == PayrollRun.Status.SENT_TO_BANK)
                .flatMap(r -> r.getLines().stream()
                        .filter(l -> staffId.equals(l.getStaffId()))
                        .map(l -> MyPayslipResponse.builder()
                                .runId(r.getId())
                                .monthLabel(r.getMonthLabel())
                                .year(r.getYear())
                                .monthIndex(r.getMonthIndex())
                                .runStatus(r.getStatus().name())
                                .basicSalary(l.getBasicSalary())
                                .taxableAllowances(l.getTaxableAllowances())
                                .nonTaxableAllowances(l.getNonTaxableAllowances())
                                .grossSalary(l.getGrossSalary())
                                .nssf(l.getNssf())
                                .nhif(l.getNhif())
                                .housingLevy(l.getHousingLevy())
                                .paye(l.getPaye())
                                .otherDeductions(l.getOtherDeductions())
                                .totalDeductions(l.getTotalDeductions())
                                .netSalary(l.getNetSalary())
                                .paymentMethod(l.getPaymentMethod())
                                .payoutDestination(l.getPayoutDestination())
                                .lineStatus(l.getStatus().name())
                                .build()))
                .sorted(Comparator.<MyPayslipResponse>comparingInt(p -> p.getYear() * 12 + p.getMonthIndex()).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Process payroll for all configured staff for the given month/year.
     * Idempotent — re-running the same month replaces the previous run.
     */
    @Transactional
    public PayrollRun processPayroll(int year, int monthIndex, String monthLabel) {
        User maker = currentUser();
        requireMaker(maker);

        payrollRunRepo.findAllByOrderByProcessedAtDesc().stream()
                .filter(r -> r.getYear() == year && r.getMonthIndex() == monthIndex && r.getStatus() != PayrollRun.Status.REJECTED)
                .findFirst()
                .ifPresent(r -> { throw new IllegalStateException(
                        monthLabel + " payroll already has an active run (" + r.getStatus() + ") — open it instead of generating a new one"); });

        List<PayeBand> bands = payeBandRepo.findAllByOrderByDisplayOrderAsc();
        List<AllowanceType> allowanceTypes = allowanceTypeRepo.findAll();
        List<StatutoryDeduction> statutoryDeductions = statutoryDeductionRepo.findAll();
        double personalRelief = getSettings().getPersonalRelief();
        List<StaffSalary> salaries = staffSalaryRepo.findAll();
        Map<String, Staff> staffByUuid = staffRepo.findAll().stream()
                .collect(Collectors.toMap(s -> s.getUuid().toString(), s -> s, (a, b) -> a));

        YearMonth runMonth = YearMonth.of(year, monthIndex + 1);
        int daysInMonth = runMonth.lengthOfMonth();
        LocalDate monthStart = runMonth.atDay(1);
        LocalDate monthEnd = runMonth.atEndOfMonth();

        PayrollRun run = PayrollRun.builder()
                .monthLabel(monthLabel)
                .year(year)
                .monthIndex(monthIndex)
                .processedAt(LocalDateTime.now())
                .lines(new ArrayList<>())
                .status(PayrollRun.Status.DRAFT)
                .makerUuid(maker.getUuid().toString())
                .makerName(displayName(maker))
                .updatedAt(LocalDateTime.now())
                .build();

        for (StaffSalary sal : salaries) {
            if (sal.getBasicSalary() <= 0) continue;

            Staff staffEntity = staffByUuid.get(sal.getStaffId());
            double prorationFactor = 1.0;
            String prorationNote = null;
            if (staffEntity != null && staffEntity.getJoined() != null) {
                LocalDate joined = staffEntity.getJoined();
                // Joined after this month entirely — not yet employed for this payroll period.
                if (joined.isAfter(monthEnd)) continue;
                if (joined.isAfter(monthStart)) {
                    int workedDays = daysInMonth - joined.getDayOfMonth() + 1;
                    prorationFactor = workedDays / (double) daysInMonth;
                    prorationNote = "Prorated " + workedDays + "/" + daysInMonth + " days (joined " + joined + ")";
                }
            }

            double taxable = 0, nonTaxable = 0, otherDed = 0;

            for (Map.Entry<Long, Double> entry : sal.getAllowances().entrySet()) {
                AllowanceType at = allowanceTypes.stream()
                        .filter(a -> a.getId().equals(entry.getKey()) && a.isActive())
                        .findFirst().orElse(null);
                if (at == null) continue;
                if (at.isTaxable()) taxable += entry.getValue();
                else nonTaxable += entry.getValue();
            }

            for (double amount : sal.getDeductions().values()) {
                otherDed += amount;
            }

            // Prorate basic + allowances for days actually worked; other (voluntary/loan) deductions
            // are fixed monthly commitments and aren't prorated.
            double basic = Math.round(sal.getBasicSalary() * prorationFactor);
            taxable = Math.round(taxable * prorationFactor);
            nonTaxable = Math.round(nonTaxable * prorationFactor);

            PayrollLineResult result = engine.calculate(
                    sal.getStaffId(), sal.getStaffId(),
                    basic, taxable, nonTaxable, otherDed,
                    bands, statutoryDeductions, personalRelief);

            StaffPaymentDetails payout = staffPaymentDetailsRepo.findByStaffId(sal.getStaffId()).orElse(null);

            PayrollLine line = PayrollLine.builder()
                    .payrollRun(run)
                    .staffId(result.getStaffId())
                    .staffName(result.getStaffName())
                    .basicSalary(result.getBasic())
                    .taxableAllowances(result.getTaxableAllowances())
                    .nonTaxableAllowances(result.getNonTaxableAllowances())
                    .grossSalary(result.getGross())
                    .nssf(result.getNssf())
                    .nhif(result.getNhif())
                    .housingLevy(result.getHousingLevy())
                    .paye(result.getPaye())
                    .otherDeductions(result.getOtherDeductions())
                    .totalDeductions(result.getTotalDeductions())
                    .netSalary(result.getNet())
                    .status(PayrollLine.Status.PENDING)
                    .paymentMethod(payout != null ? payout.getPaymentMethod().name() : null)
                    .payoutDestination(describePayout(payout))
                    .prorationNote(prorationNote)
                    .build();

            run.getLines().add(line);
        }

        return payrollRunRepo.save(run);
    }

    private String describePayout(StaffPaymentDetails payout) {
        if (payout == null) return null;
        return switch (payout.getPaymentMethod()) {
            case BANK_TRANSFER -> (payout.getBank() != null ? payout.getBank().getName() : "Bank") +
                    (payout.getBankAccountNumber() != null ? " - " + maskAccount(payout.getBankAccountNumber()) : "");
            case MOBILE_MONEY -> (payout.getMobileMoneyProvider() != null ? payout.getMobileMoneyProvider().getName() : "Mobile Money") +
                    (payout.getMobileNumber() != null ? " - " + payout.getMobileNumber() : "");
            case CHEQUE -> "Cheque";
        };
    }

    private String maskAccount(String accountNumber) {
        if (accountNumber.length() <= 4) return accountNumber;
        return "****" + accountNumber.substring(accountNumber.length() - 4);
    }

    @Transactional
    public PayrollRun submitRun(Long runId) {
        User actor = currentUser();
        requireMaker(actor);

        PayrollRun run = getRun(runId);
        if (run.getStatus() != PayrollRun.Status.DRAFT)
            throw new IllegalStateException("Only draft runs can be submitted for approval");

        // Re-check live (not the stale snapshot taken at generation time) so payout details added after
        // the draft was generated are picked up, and refresh each line's snapshot while we're at it.
        List<String> missingPayout = new ArrayList<>();
        for (PayrollLine line : run.getLines()) {
            StaffPaymentDetails payout = staffPaymentDetailsRepo.findByStaffId(line.getStaffId()).orElse(null);
            if (payout == null) {
                missingPayout.add(line.getStaffName());
            } else {
                line.setPaymentMethod(payout.getPaymentMethod().name());
                line.setPayoutDestination(describePayout(payout));
            }
        }
        if (!missingPayout.isEmpty()) {
            throw new IllegalStateException("Cannot submit for approval — payment details missing for: " +
                    String.join(", ", missingPayout) + ". Set these up under Staff Payment Details first.");
        }

        List<PayrollWorkflowStep> steps = applicableSteps(run);
        if (steps.isEmpty())
            throw new IllegalStateException("No approval steps apply to a payroll run of this size — check your workflow setup and thresholds");

        LocalDateTime now = LocalDateTime.now();
        run.setStatus(PayrollRun.Status.PENDING_APPROVAL);
        run.setSubmittedAt(now);
        run.setUpdatedAt(now);
        run.setCurrentStepOrder(steps.get(0).getSequenceOrder());
        run.setRejectionReason(null);
        PayrollRun saved = payrollRunRepo.save(run);
        addApproval(saved, steps.get(0).getSequenceOrder(), steps.get(0).getLabel(), actor, PayrollRunApproval.Action.SUBMITTED, null);
        return saved;
    }

    @Transactional
    public PayrollRun decideStep(Long runId, PayrollDecisionRequest req) {
        PayrollRun run = getRun(runId);
        if (run.getStatus() != PayrollRun.Status.PENDING_APPROVAL)
            throw new IllegalStateException("This run is not awaiting approval");

        User actor = currentUser();
        String actorUuid = actor.getUuid().toString();

        PayrollWorkflowStep step = workflowStepRepo.findAllByActiveTrueOrderBySequenceOrderAsc().stream()
                .filter(s -> run.getCurrentStepOrder() != null && s.getSequenceOrder() == run.getCurrentStepOrder())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Current workflow step no longer exists"));

        boolean isAssigned = step.getAssignedUsers().stream().anyMatch(u -> u.getUuid().toString().equals(actorUuid));
        if (!isAssigned)
            throw new IllegalStateException("You are not an assigned approver for the current step (" + step.getLabel() + ")");

        // Self-approval is blocked, except for a sole approver: someone who is the only person assigned
        // to this step and also holds maker rights. Requiring a second person in that setup would make
        // the run un-actionable by anyone, so a lone maker-approver may act on their own submission.
        boolean isSoleApproverOnStep = step.getAssignedUsers().size() == 1;
        if (actorUuid.equals(run.getMakerUuid()) && !isSoleApproverOnStep)
            throw new IllegalStateException("The person who generated this run cannot approve or reject it");

        LocalDateTime now = LocalDateTime.now();

        if (req.getDecision() == PayrollDecisionRequest.Decision.REJECT) {
            if (req.getComment() == null || req.getComment().isBlank())
                throw new IllegalArgumentException("A reason is required to reject a payroll run");
            run.setStatus(PayrollRun.Status.REJECTED);
            run.setRejectionReason(req.getComment());
            run.setCurrentStepOrder(null);
            run.setUpdatedAt(now);
            PayrollRun saved = payrollRunRepo.save(run);
            addApproval(saved, step.getSequenceOrder(), step.getLabel(), actor, PayrollRunApproval.Action.REJECTED, req.getComment());
            return saved;
        }

        addApproval(run, step.getSequenceOrder(), step.getLabel(), actor, PayrollRunApproval.Action.APPROVED, req.getComment());

        boolean stepCleared;
        if (step.getApprovalRule() == PayrollWorkflowStep.ApprovalRule.ANY_ONE_APPROVES) {
            stepCleared = true;
        } else {
            List<PayrollRunApproval> approvals = approvalRepo.findByPayrollRunIdAndStepOrderAndAction(
                    run.getId(), step.getSequenceOrder(), PayrollRunApproval.Action.APPROVED);
            Set<String> approvedUuids = approvals.stream().map(PayrollRunApproval::getActorUuid).collect(Collectors.toSet());
            long approvedCount = step.getAssignedUsers().stream()
                    .filter(u -> approvedUuids.contains(u.getUuid().toString())).count();
            stepCleared = step.getApprovalRule() == PayrollWorkflowStep.ApprovalRule.AT_LEAST
                    ? approvedCount >= step.getMinApprovals()
                    : approvedCount >= step.getAssignedUsers().size();
        }

        if (stepCleared) {
            Optional<PayrollWorkflowStep> next = applicableSteps(run).stream()
                    .filter(s -> s.getSequenceOrder() > step.getSequenceOrder())
                    .findFirst();
            if (next.isPresent()) {
                run.setCurrentStepOrder(next.get().getSequenceOrder());
            } else {
                run.setStatus(PayrollRun.Status.APPROVED);
                run.setCurrentStepOrder(null);
            }
        }
        run.setUpdatedAt(now);
        return payrollRunRepo.save(run);
    }

    @Transactional
    public PayrollRun markSentToBank(Long runId) {
        PayrollRun run = getRun(runId);
        if (run.getStatus() != PayrollRun.Status.APPROVED)
            throw new IllegalStateException("Only fully approved runs can be marked as sent to the bank");

        User actor = currentUser();
        LocalDateTime now = LocalDateTime.now();
        run.setStatus(PayrollRun.Status.SENT_TO_BANK);
        run.setSentToBankByUuid(actor.getUuid().toString());
        run.setSentToBankByName(displayName(actor));
        run.setSentToBankAt(now);
        run.setUpdatedAt(now);
        run.getLines().forEach(l -> l.setStatus(PayrollLine.Status.PAID));
        PayrollRun saved = payrollRunRepo.save(run);
        addApproval(saved, null, null, actor, PayrollRunApproval.Action.SENT_TO_BANK, null);
        return saved;
    }

    private void addApproval(PayrollRun run, Integer stepOrder, String stepLabel, User actor,
                              PayrollRunApproval.Action action, String comment) {
        approvalRepo.save(PayrollRunApproval.builder()
                .payrollRun(run)
                .stepOrder(stepOrder)
                .stepLabel(stepLabel)
                .actorUuid(actor.getUuid().toString())
                .actorName(displayName(actor))
                .action(action)
                .comment(comment)
                .actedAt(LocalDateTime.now())
                .build());
    }

    private User currentUser() {
        String username = userUtils.getCurrentUser();
        if (username == null) throw new IllegalStateException("Not authenticated");
        return userRepo.findByUserName(username)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found: " + username));
    }

    private String displayName(User u) {
        String name = ((u.getFirstName() != null ? u.getFirstName() : "") + " " +
                (u.getOtherNames() != null ? u.getOtherNames() : "")).trim();
        return name.isEmpty() ? u.getUserName() : name;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private PayrollSettings defaultSettings() {
        return PayrollSettings.builder()
                .personalRelief(2400)
                .insuranceRelief(5000)
                .payDay(28)
                .paymentMethod("bank_transfer")
                .currency("KES")
                .build();
    }
}
