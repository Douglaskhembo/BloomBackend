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
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    // ── Fee Items (Structure) ─────────────────────────────────────────────────

    public List<FeeItem> getAllFeeItems() {
        return feeItemRepo.findAll();
    }

    public List<FeeItem> getFeeItemsByGrade(String grade) {
        return feeItemRepo.findByGradeOrGradeIsNull(grade);
    }

    @Transactional
    public FeeItem createFeeItem(FeeItemRequest req) {
        return feeItemRepo.save(FeeItem.builder()
                .name(req.getName())
                .description(req.getDescription())
                .amount(req.getAmount())
                .grade(req.getGrade())
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
        f.setGrade(req.getGrade());
        f.setTerm(req.getTerm());
        f.setActive(req.isActive());
        return feeItemRepo.save(f);
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
                .build();
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

    @Transactional
    public FeeStructure saveDraft(FeeStructureSubmitRequest req) {
        LocalDateTime now = LocalDateTime.now();
        FeeStructure fs = FeeStructure.builder()
                .academicYear(req.getAcademicYear())
                .grade(req.getGrade())
                .term(req.getTerm())
                .version(nextVersion(req.getGrade(), req.getTerm()))
                .status(FeeStructure.Status.DRAFT)
                .lines(toLines(req.getLines()))
                .baseline(computeBaseline(req.getGrade(), req.getTerm()))
                .maker(req.getMaker())
                .note(req.getNote())
                .submittedAt(now)
                .updatedAt(now)
                .build();
        FeeStructure saved = feeStructureRepo.save(fs);
        addStructureAudit(req.getMaker(), FeeStructureAudit.Action.SAVED_DRAFT, req.getGrade(), req.getTerm(), req.getAcademicYear(), req.getNote());
        return saved;
    }

    @Transactional
    public FeeStructure submitForApproval(FeeStructureSubmitRequest req) {
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
            fs.setMaker(req.getMaker());
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
                    .maker(req.getMaker())
                    .note(req.getNote())
                    .submittedAt(now)
                    .updatedAt(now)
                    .build();
        }

        FeeStructure saved = feeStructureRepo.save(fs);
        addStructureAudit(req.getMaker(), isRework ? FeeStructureAudit.Action.REWORKED : FeeStructureAudit.Action.SUBMITTED,
                req.getGrade(), req.getTerm(), req.getAcademicYear(), req.getNote());
        return saved;
    }

    @Transactional
    public FeeStructure approveStructure(UUID uuid, String approver) {
        FeeStructure fs = feeStructureRepo.findByUuid(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Fee structure not found"));
        if (fs.getStatus() != FeeStructure.Status.PENDING_APPROVAL)
            throw new IllegalArgumentException("Only fee structures pending approval can be approved");
        LocalDateTime now = LocalDateTime.now();
        fs.setStatus(FeeStructure.Status.APPROVED);
        fs.setApprover(approver);
        fs.setReviewedAt(now);
        fs.setUpdatedAt(now);
        FeeStructure saved = feeStructureRepo.save(fs);
        addStructureAudit(approver, FeeStructureAudit.Action.APPROVED, fs.getGrade(), fs.getTerm(), fs.getAcademicYear(), null);
        return saved;
    }

    @Transactional
    public FeeStructure rejectStructure(UUID uuid, String approver, String reason) {
        FeeStructure fs = feeStructureRepo.findByUuid(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Fee structure not found"));
        if (fs.getStatus() != FeeStructure.Status.PENDING_APPROVAL)
            throw new IllegalArgumentException("Only fee structures pending approval can be rejected");
        LocalDateTime now = LocalDateTime.now();
        fs.setStatus(FeeStructure.Status.REJECTED);
        fs.setApprover(approver);
        fs.setRejectionReason(reason);
        fs.setReviewedAt(now);
        fs.setUpdatedAt(now);
        FeeStructure saved = feeStructureRepo.save(fs);
        addStructureAudit(approver, FeeStructureAudit.Action.REJECTED, fs.getGrade(), fs.getTerm(), fs.getAcademicYear(), reason);
        return saved;
    }
}
