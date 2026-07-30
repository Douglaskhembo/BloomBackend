package com.bloom.bloomschool.fees.service;

import com.bloom.bloomschool.fees.dto.FeeItemRequest;
import com.bloom.bloomschool.fees.dto.FeePaymentRequest;
import com.bloom.bloomschool.fees.dto.FeeStructureLineRequest;
import com.bloom.bloomschool.fees.dto.FeeStructureSubmitRequest;
import com.bloom.bloomschool.fees.entity.FeeItem;
import com.bloom.bloomschool.fees.entity.FeePayment;
import com.bloom.bloomschool.fees.entity.FeeStructure;
import com.bloom.bloomschool.fees.entity.FeeStructureAudit;
import com.bloom.bloomschool.fees.entity.FeeStructureLine;
import com.bloom.bloomschool.fees.repository.FeeItemRepository;
import com.bloom.bloomschool.fees.repository.FeePaymentRepository;
import com.bloom.bloomschool.fees.repository.FeeStructureAuditRepository;
import com.bloom.bloomschool.fees.repository.FeeStructureRepository;
import com.bloom.bloomschool.school.entity.GradeLevel;
import com.bloom.bloomschool.school.repository.GradeLevelRepository;
import com.bloom.bloomschool.auth.model.User;
import com.bloom.bloomschool.auth.repo.UserRepository;
import com.bloom.bloomschool.auth.service.PermissionResolver;
import com.bloom.bloomschool.common.utils.UserUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeeService {

    private final FeeItemRepository feeItemRepo;
    private final FeePaymentRepository feePaymentRepo;
    private final FeeStructureRepository feeStructureRepo;
    private final FeeStructureAuditRepository feeStructureAuditRepo;
    private final GradeLevelRepository gradeLevelRepo;
    private final UserRepository userRepo;
    private final UserUtils userUtils;
    private final PermissionResolver permissionResolver;

    // ── Fee Items (Structure) ─────────────────────────────────────────────────

    public List<FeeItem> getAllFeeItems() {
        return feeItemRepo.findAll();
    }

    public List<FeeItem> getFeeItemsByGrade(String grade) {
        return feeItemRepo.findAll().stream()
                .filter(item -> item.getGradeLevels().isEmpty()
                        || item.getGradeLevels().stream().anyMatch(g -> g.getName().equals(grade)))
                .toList();
    }

    @Transactional
    public FeeItem createFeeItem(FeeItemRequest req) {
        return feeItemRepo.save(FeeItem.builder()
                .name(req.getName())
                .description(req.getDescription())
                .amount(req.getAmount())
                .gradeLevels(resolveGradeLevels(req.getGradeLevelUuids()))
                .term(req.getTerm())
                .active(req.isActive())
                .build());
    }

    @Transactional
    public FeeItem updateFeeItem(Long id, FeeItemRequest req) {
        FeeItem f = feeItemRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Fee item not found"));
        f.setName(req.getName());
        f.setDescription(req.getDescription());
        f.setAmount(req.getAmount());
        f.setGradeLevels(resolveGradeLevels(req.getGradeLevelUuids()));
        f.setTerm(req.getTerm());
        f.setActive(req.isActive());
        return feeItemRepo.save(f);
    }

    private Set<GradeLevel> resolveGradeLevels(Set<UUID> uuids) {
        if (uuids == null || uuids.isEmpty()) return new HashSet<>();
        return new HashSet<>(gradeLevelRepo.findAllByUuidIn(uuids));
    }

    @Transactional
    public FeeItem toggleFeeItem(Long id) {
        FeeItem f = feeItemRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Fee item not found"));
        f.setActive(!f.isActive());
        return feeItemRepo.save(f);
    }

    @Transactional
    public void deleteFeeItem(Long id) {
        feeItemRepo.deleteById(id);
    }

    // ── Fee Payments ──────────────────────────────────────────────────────────

    public List<FeePayment> getAllPayments() {
        return feePaymentRepo.findAllOrderByDateDesc();
    }

    public List<FeePayment> getPaymentsByStudent(String studentId) {
        return feePaymentRepo.findByStudentIdOrderByPaymentDateDesc(studentId);
    }

    public double getTotalPaidByStudent(String studentId) {
        Double total = feePaymentRepo.sumAmountByStudentId(studentId);
        return total != null ? total : 0.0;
    }

    /**
     * This is the staff-facing manual capture endpoint — walk-in cash, cheque, or bank-slip payments
     * (and a fallback for M-Pesa/bank transfers when the live gateway/webhook integration is down).
     * Gateway-matched payments are created directly by PaymentReconciliationService instead, never
     * through here, so everything recorded via this path is tagged {@code source = MANUAL}.
     */
    @Transactional
    public FeePayment recordPayment(FeePaymentRequest req) {
        if (feePaymentRepo.existsByReference(req.getReference()))
            throw new IllegalArgumentException("Payment reference '" + req.getReference() + "' already exists");

        FeePayment p = FeePayment.builder()
                .studentId(req.getStudentId())
                .studentName(req.getStudentName())
                .grade(req.getGrade())
                .stream(req.getStream())
                .amount(req.getAmount())
                .expectedAmount(req.getExpectedAmount() != null ? req.getExpectedAmount() : 0)
                .method(req.getMethod())
                .reference(req.getReference())
                .paymentDate(req.getPaymentDate() != null ? req.getPaymentDate() : LocalDateTime.now())
                .source(FeePayment.PaymentSource.MANUAL)
                .verificationStatus(defaultVerificationStatus(req.getMethod()))
                .bankName(req.getBankName())
                .slipOrChequeNumber(req.getSlipOrChequeNumber())
                .notes(req.getNotes())
                .build();
        return feePaymentRepo.save(p);
    }

    /** Cash/card settle in the cashier's hand immediately; everything else needs a second-person check. */
    private FeePayment.VerificationStatus defaultVerificationStatus(FeePayment.PaymentMethod method) {
        return (method == FeePayment.PaymentMethod.CASH || method == FeePayment.PaymentMethod.CARD)
                ? FeePayment.VerificationStatus.CONFIRMED
                : FeePayment.VerificationStatus.PENDING_VERIFICATION;
    }

    public List<FeePayment> getPendingVerificationPayments() {
        return feePaymentRepo.findBySourceAndVerificationStatusOrderByPaymentDateDesc(
                FeePayment.PaymentSource.MANUAL, FeePayment.VerificationStatus.PENDING_VERIFICATION);
    }

    @Transactional
    public FeePayment verifyPayment(Long id, String verifier) {
        FeePayment p = feePaymentRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found"));
        if (p.getVerificationStatus() != FeePayment.VerificationStatus.PENDING_VERIFICATION)
            throw new IllegalArgumentException("Only payments pending verification can be verified");
        p.setVerificationStatus(FeePayment.VerificationStatus.CONFIRMED);
        p.setVerifiedBy(verifier);
        p.setVerifiedAt(LocalDateTime.now());
        return feePaymentRepo.save(p);
    }

    @Transactional
    public FeePayment rejectPayment(Long id, String verifier, String reason) {
        FeePayment p = feePaymentRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found"));
        if (p.getVerificationStatus() != FeePayment.VerificationStatus.PENDING_VERIFICATION)
            throw new IllegalArgumentException("Only payments pending verification can be rejected");
        p.setVerificationStatus(FeePayment.VerificationStatus.REJECTED);
        p.setVerifiedBy(verifier);
        p.setVerifiedAt(LocalDateTime.now());
        p.setRejectionReason(reason);
        return feePaymentRepo.save(p);
    }

    @Transactional
    public void deletePayment(Long id) {
        feePaymentRepo.deleteById(id);
    }

    // ── Fee Structures (Maker / Approver / Approved workflow) ───────────────────

    public List<FeeStructure> getAllFeeStructures() {
        return feeStructureRepo.findAllByOrderBySubmittedAtDesc();
    }

    public List<FeeStructureAudit> getFeeStructureAudit() {
        return feeStructureAuditRepo.findAllByOrderByAtDesc();
    }

    private List<FeeStructureLine> defaultLines() {
        return feeItemRepo.findAll().stream()
                .map(item -> FeeStructureLine.builder().itemId(item.getId()).enabled(item.isActive()).amount(item.getAmount()).build())
                .toList();
    }

    private List<FeeStructureLine> mergeWithCurrentItems(List<FeeStructureLine> existing) {
        List<FeeItem> allItems = feeItemRepo.findAll();
        Set<Long> knownIds = existing.stream().map(FeeStructureLine::getItemId).collect(Collectors.toSet());
        List<FeeStructureLine> merged = new ArrayList<>();
        for (FeeStructureLine line : existing) {
            if (allItems.stream().anyMatch(item -> item.getId().equals(line.getItemId()))) merged.add(line);
        }
        for (FeeItem item : allItems) {
            if (!knownIds.contains(item.getId()))
                merged.add(FeeStructureLine.builder().itemId(item.getId()).enabled(false).amount(item.getAmount()).build());
        }
        return merged;
    }

    private List<FeeStructureLine> computeBaseline(String grade, String term) {
        return feeStructureRepo.findFirstByGradeAndTermAndStatusOrderByReviewedAtDesc(grade, term, FeeStructure.Status.APPROVED)
                .map(fs -> mergeWithCurrentItems(fs.getLines()))
                .orElseGet(this::defaultLines);
    }

    private List<FeeStructureLine> toLines(List<FeeStructureLineRequest> lines) {
        return lines.stream()
                .map(l -> FeeStructureLine.builder().itemId(l.getItemId()).enabled(l.isEnabled()).amount(l.getAmount()).build())
                .toList();
    }

    private int nextVersion(String grade, String term) {
        return (int) feeStructureRepo.countByGradeAndTermAndStatus(grade, term, FeeStructure.Status.APPROVED) + 1;
    }

    private void addStructureAudit(String actor, FeeStructureAudit.Action action, String grade, String term, Integer academicYear, String comment) {
        feeStructureAuditRepo.save(FeeStructureAudit.builder()
                .at(LocalDateTime.now())
                .actor(actor)
                .action(action)
                .grade(grade)
                .term(term)
                .academicYear(academicYear)
                .comment(comment)
                .build());
    }

    private User currentUser() {
        String username = userUtils.getCurrentUser();
        if (username == null) throw new IllegalStateException("Not authenticated");
        return userRepo.findByUserName(username)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found: " + username));
    }

    /**
     * Blocks a maker from approving/rejecting their own submission, except when they're the only
     * user currently holding FEES_APPROVE — otherwise a single-admin school could never move a fee
     * structure past submission. Mirrors PayrollService.decideStep's "sole approver" exception.
     */
    private void requireNotSelfApproval(FeeStructure fs, User actor) {
        if (fs.getMaker().equals(actor.getUserName())
                && permissionResolver.anyOtherUserHasPermission("FEES_APPROVE", actor.getUserName())) {
            throw new IllegalStateException("The person who submitted this fee structure cannot approve or reject it themselves");
        }
    }

    @Transactional
    public FeeStructure saveDraft(FeeStructureSubmitRequest req) {
        String maker = currentUser().getUserName();
        LocalDateTime now = LocalDateTime.now();
        FeeStructure fs = FeeStructure.builder()
                .academicYear(req.getAcademicYear())
                .grade(req.getGrade())
                .term(req.getTerm())
                .version(nextVersion(req.getGrade(), req.getTerm()))
                .status(FeeStructure.Status.DRAFT)
                .lines(toLines(req.getLines()))
                .baseline(computeBaseline(req.getGrade(), req.getTerm()))
                .maker(maker)
                .note(req.getNote())
                .submittedAt(now)
                .updatedAt(now)
                .build();
        FeeStructure saved = feeStructureRepo.save(fs);
        addStructureAudit(maker, FeeStructureAudit.Action.SAVED_DRAFT, req.getGrade(), req.getTerm(), req.getAcademicYear(), req.getNote());
        return saved;
    }

    @Transactional
    public FeeStructure submitForApproval(FeeStructureSubmitRequest req) {
        String maker = currentUser().getUserName();
        LocalDateTime now = LocalDateTime.now();
        boolean isRework = req.getReworkUuid() != null;
        FeeStructure fs;

        if (isRework) {
            fs = feeStructureRepo.findByUuid(req.getReworkUuid())
                    .orElseThrow(() -> new EntityNotFoundException("Fee structure not found"));
            if (fs.getStatus() != FeeStructure.Status.REJECTED)
                throw new IllegalArgumentException("Only rejected fee structures can be reworked");
            fs.setLines(toLines(req.getLines()));
            fs.setBaseline(computeBaseline(req.getGrade(), req.getTerm()));
            fs.setNote(req.getNote());
            fs.setMaker(maker);
            fs.setStatus(FeeStructure.Status.PENDING_APPROVAL);
            fs.setRejectionReason(null);
            fs.setSubmittedAt(now);
            fs.setUpdatedAt(now);
        } else {
            fs = FeeStructure.builder()
                    .academicYear(req.getAcademicYear())
                    .grade(req.getGrade())
                    .term(req.getTerm())
                    .version(nextVersion(req.getGrade(), req.getTerm()))
                    .status(FeeStructure.Status.PENDING_APPROVAL)
                    .lines(toLines(req.getLines()))
                    .baseline(computeBaseline(req.getGrade(), req.getTerm()))
                    .maker(maker)
                    .note(req.getNote())
                    .submittedAt(now)
                    .updatedAt(now)
                    .build();
        }

        FeeStructure saved = feeStructureRepo.save(fs);
        addStructureAudit(maker, isRework ? FeeStructureAudit.Action.REWORKED : FeeStructureAudit.Action.SUBMITTED,
                req.getGrade(), req.getTerm(), req.getAcademicYear(), req.getNote());
        return saved;
    }

    @Transactional
    public FeeStructure approveStructure(UUID uuid) {
        FeeStructure fs = feeStructureRepo.findByUuid(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Fee structure not found"));
        if (fs.getStatus() != FeeStructure.Status.PENDING_APPROVAL)
            throw new IllegalArgumentException("Only fee structures pending approval can be approved");
        User actor = currentUser();
        requireNotSelfApproval(fs, actor);
        LocalDateTime now = LocalDateTime.now();
        fs.setStatus(FeeStructure.Status.APPROVED);
        fs.setApprover(actor.getUserName());
        fs.setReviewedAt(now);
        fs.setUpdatedAt(now);
        FeeStructure saved = feeStructureRepo.save(fs);
        addStructureAudit(actor.getUserName(), FeeStructureAudit.Action.APPROVED, fs.getGrade(), fs.getTerm(), fs.getAcademicYear(), null);
        return saved;
    }

    @Transactional
    public FeeStructure rejectStructure(UUID uuid, String reason) {
        FeeStructure fs = feeStructureRepo.findByUuid(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Fee structure not found"));
        if (fs.getStatus() != FeeStructure.Status.PENDING_APPROVAL)
            throw new IllegalArgumentException("Only fee structures pending approval can be rejected");
        User actor = currentUser();
        requireNotSelfApproval(fs, actor);
        LocalDateTime now = LocalDateTime.now();
        fs.setStatus(FeeStructure.Status.REJECTED);
        fs.setApprover(actor.getUserName());
        fs.setRejectionReason(reason);
        fs.setReviewedAt(now);
        fs.setUpdatedAt(now);
        FeeStructure saved = feeStructureRepo.save(fs);
        addStructureAudit(actor.getUserName(), FeeStructureAudit.Action.REJECTED, fs.getGrade(), fs.getTerm(), fs.getAcademicYear(), reason);
        return saved;
    }
}
