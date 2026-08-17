package com.bloom.bloomschool.fees.service;

import com.bloom.bloomschool.fees.dto.FeeItemRequest;
import com.bloom.bloomschool.fees.dto.FeePaymentRequest;
import com.bloom.bloomschool.fees.dto.FeeStructureLineRequest;
import com.bloom.bloomschool.fees.dto.FeeStructureSubmitRequest;
import com.bloom.bloomschool.fees.entity.FeeCategory;
import com.bloom.bloomschool.fees.entity.FeeItem;
import com.bloom.bloomschool.fees.entity.FeePayment;
import com.bloom.bloomschool.fees.entity.FeeStructure;
import com.bloom.bloomschool.fees.entity.FeeStructureAudit;
import com.bloom.bloomschool.fees.entity.FeeStructureLine;
import com.bloom.bloomschool.fees.entity.StudentFeeCharge;
import com.bloom.bloomschool.fees.repository.FeeItemRepository;
import com.bloom.bloomschool.fees.repository.FeePaymentRepository;
import com.bloom.bloomschool.fees.repository.FeeStructureAuditRepository;
import com.bloom.bloomschool.fees.repository.FeeStructureRepository;
import com.bloom.bloomschool.fees.repository.StudentFeeChargeRepository;
import com.bloom.bloomschool.calendar.entity.TermPeriod;
import com.bloom.bloomschool.calendar.repository.TermPeriodRepository;
import com.bloom.bloomschool.school.entity.GradeLevel;
import com.bloom.bloomschool.school.repository.GradeLevelRepository;
import com.bloom.bloomschool.students.entity.Student;
import com.bloom.bloomschool.students.repository.StudentRepository;
import com.bloom.bloomschool.students.util.BoarderStatus;
import com.bloom.bloomschool.transport.repository.StudentRouteRepository;
import com.bloom.bloomschool.auth.model.User;
import com.bloom.bloomschool.auth.repo.UserRepository;
import com.bloom.bloomschool.auth.service.PermissionResolver;
import com.bloom.bloomschool.common.utils.UserUtils;
import com.bloom.bloomschool.setups.service.RefGeneratorService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    private final StudentFeeChargeRepository studentFeeChargeRepo;
    private final TermPeriodRepository termPeriodRepo;
    private final GradeLevelRepository gradeLevelRepo;
    private final StudentRepository studentRepo;
    private final StudentRouteRepository studentRouteRepo;
    private final UserRepository userRepo;
    private final UserUtils userUtils;
    private final PermissionResolver permissionResolver;
    private final RefGeneratorService refGeneratorService;

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
                .term1Amount(req.getTerm1Amount())
                .term2Amount(req.getTerm2Amount())
                .term3Amount(req.getTerm3Amount())
                .category(req.getCategory())
                .mandatory(req.getMandatory())
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
        f.setTerm1Amount(req.getTerm1Amount());
        f.setTerm2Amount(req.getTerm2Amount());
        f.setTerm3Amount(req.getTerm3Amount());
        f.setCategory(req.getCategory());
        f.setMandatory(req.getMandatory());
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

    public List<FeePayment> getPaymentsByAdmission(UUID admissionUuid) {
        return feePaymentRepo.findByAdmissionUuidOrderByPaymentDateDesc(admissionUuid);
    }

    /** Used to gate an admission's FEE_PAYMENT → ENROLLED transition — see StudentService.updateAdmissionStage. */
    public boolean hasConfirmedPayment(UUID admissionUuid) {
        return feePaymentRepo.existsByAdmissionUuidAndVerificationStatus(admissionUuid, FeePayment.VerificationStatus.CONFIRMED);
    }

    @Transactional
    public void adoptAdmissionPayments(UUID admissionUuid, String studentId, String studentName, String grade, String stream) {
        List<FeePayment> payments = feePaymentRepo.findByAdmissionUuidOrderByPaymentDateDesc(admissionUuid);
        for (FeePayment p : payments) {
            p.setStudentId(studentId);
            if (p.getStudentName() == null) p.setStudentName(studentName);
            if (p.getGrade() == null) p.setGrade(grade);
            if (p.getStream() == null) p.setStream(stream);
        }
        feePaymentRepo.saveAll(payments);
    }

    public double getTotalPaidByStudent(String studentId) {
        Double total = feePaymentRepo.sumAmountByStudentId(studentId);
        return total != null ? total : 0.0;
    }

    @Transactional
    public FeePayment recordPayment(FeePaymentRequest req) {
        boolean hasStudentId = req.getStudentId() != null && !req.getStudentId().isBlank();
        if (!hasStudentId && req.getAdmissionUuid() == null)
            throw new IllegalArgumentException("Either studentId or admissionUuid is required");

        boolean isCash = req.getMethod() == FeePayment.PaymentMethod.CASH;
        String reference;
        if (isCash) {
            // Cash walk-ins have no external slip/txn number to key off — mint one ourselves so the
            // teller never has to make up a reference.
            reference = refGeneratorService.generateReference("CASH");
        } else {
            if (req.getReference() == null || req.getReference().isBlank())
                throw new IllegalArgumentException("Reference is required for " + req.getMethod());
            reference = req.getReference();
            if (feePaymentRepo.existsByReference(reference))
                throw new IllegalArgumentException("Payment reference '" + reference + "' already exists");
        }

        FeePayment p = FeePayment.builder()
                .studentId(req.getStudentId())
                .admissionUuid(req.getAdmissionUuid())
                .studentName(req.getStudentName())
                .grade(req.getGrade())
                .stream(req.getStream())
                .amount(req.getAmount())
                .expectedAmount(req.getExpectedAmount() != null ? req.getExpectedAmount() : 0)
                .method(req.getMethod())
                .reference(reference)
                .receiptNumber(refGeneratorService.generateReference("RCPT"))
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

    // Fee Structures (Maker / Approver / Approved workflow)

    public List<FeeStructure> getAllFeeStructures() {
        return feeStructureRepo.findAllByOrderBySubmittedAtDesc();
    }

    public List<FeeStructureAudit> getFeeStructureAudit() {
        return feeStructureAuditRepo.findAllByOrderByAtDesc();
    }

    //Student Fee Charges

    @Transactional
    public List<StudentFeeCharge> getCurrentCharges(String admissionNumber, String grade) {
        List<FeeStructure> approved = feeStructureRepo.findAllByStatus(FeeStructure.Status.APPROVED);
        Map<String, FeeStructure> latestByPeriod = new HashMap<>();
        for (FeeStructure fs : approved) {
            String key = fs.getAcademicYear() + "::" + fs.getGrade() + "::" + fs.getTerm();
            FeeStructure existing = latestByPeriod.get(key);
            if (existing == null || reviewedAtOf(fs).isAfter(reviewedAtOf(existing))) latestByPeriod.put(key, fs);
        }

        Set<UUID> currentUuids = new HashSet<>();
        for (FeeStructure fs : latestByPeriod.values()) {
            if (!periodHasStarted(fs.getAcademicYear(), fs.getTerm())) continue;
            generateCharges(fs);
            currentUuids.add(fs.getUuid());
        }

        if (currentUuids.isEmpty()) return List.of();
        List<StudentFeeCharge> charges = admissionNumber != null
                ? studentFeeChargeRepo.findByAdmissionNumberAndFeeStructureUuidIn(admissionNumber, currentUuids)
                : studentFeeChargeRepo.findByFeeStructureUuidIn(currentUuids);
        return grade != null ? charges.stream().filter(c -> c.getGrade().equals(grade)).toList() : charges;
    }

    private LocalDateTime reviewedAtOf(FeeStructure fs) {
        return fs.getReviewedAt() != null ? fs.getReviewedAt() : fs.getUpdatedAt();
    }


    private Optional<LocalDate> periodStartDate(int academicYear, String term) {
        if ("Full Year".equals(term))
            return termPeriodRepo.findByAcademicYear(academicYear).stream().map(TermPeriod::getStartDate).min(LocalDate::compareTo);
        return termPeriodRepo.findByAcademicYearAndTerm(academicYear, term).map(TermPeriod::getStartDate);
    }

    private Optional<LocalDate> periodEndDate(int academicYear, String term) {
        if ("Full Year".equals(term))
            return termPeriodRepo.findByAcademicYear(academicYear).stream().map(TermPeriod::getEndDate).max(LocalDate::compareTo);
        return termPeriodRepo.findByAcademicYearAndTerm(academicYear, term).map(TermPeriod::getEndDate);
    }


    private boolean periodHasStarted(int academicYear, String term) {
        return periodStartDate(academicYear, term).map(start -> !start.isAfter(LocalDate.now())).orElse(true);
    }

    private LocalDate joinDateOf(Student s) {
        if (s.getJoinDate() != null) return s.getJoinDate();
        return s.getCreatedDate() != null
                ? s.getCreatedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                : LocalDate.MIN;
    }

    private void generateCharges(FeeStructure fs) {
        List<FeeStructureLine> enabled = fs.getLines().stream().filter(FeeStructureLine::isEnabled).toList();
        if (enabled.isEmpty()) return;

        List<Student> gradeStudents = studentRepo.findByGradeAndStatus(fs.getGrade(), Student.Status.ACTIVE);
        if (gradeStudents.isEmpty()) return;

        LocalDate periodEnd = periodEndDate(fs.getAcademicYear(), fs.getTerm()).orElse(null);
        if (periodEnd != null) gradeStudents = gradeStudents.stream().filter(s -> !joinDateOf(s).isAfter(periodEnd)).toList();
        if (gradeStudents.isEmpty()) return;

        Map<Long, FeeItem> itemsById = feeItemRepo.findAllById(
                enabled.stream().map(FeeStructureLine::getItemId).toList()
        ).stream().collect(Collectors.toMap(FeeItem::getId, i -> i));

        Set<String> existingKeys = studentFeeChargeRepo.findByFeeStructureUuid(fs.getUuid()).stream()
                .map(c -> c.getStudentUuid() + "::" + c.getItemId())
                .collect(Collectors.toSet());

        LocalDateTime now = LocalDateTime.now();
        List<StudentFeeCharge> charges = new ArrayList<>();
        for (FeeStructureLine line : enabled) {
            FeeItem item = itemsById.get(line.getItemId());   // may be null if the item was since deleted
            String itemName = item != null ? item.getName() : ("Item #" + line.getItemId());
            FeeCategory category = item != null ? item.getCategory() : null;

            for (Student s : gradeStudents) {
                if (!isEligible(s, category)) continue;
                if (existingKeys.contains(s.getUuid() + "::" + line.getItemId())) continue;
                charges.add(StudentFeeCharge.builder()
                        .studentUuid(s.getUuid()).admissionNumber(s.getAdmissionNumber())
                        .studentName(s.getFirstName() + " " + s.getLastName())
                        .grade(s.getGrade()).stream(s.getStream())
                        .feeStructureUuid(fs.getUuid()).structureVersion(fs.getVersion())
                        .academicYear(fs.getAcademicYear()).period(fs.getTerm())
                        .itemId(line.getItemId()).itemName(itemName).category(category)
                        .amount(line.getAmount()).dueDate(fs.getDueDate()).generatedAt(now)
                        .build());
            }
        }
        studentFeeChargeRepo.saveAll(charges);
    }

    private boolean isEligible(Student s, FeeCategory category) {
        if (category == FeeCategory.BOARDING) return s.getBoarderStatus() == BoarderStatus.BOARDER;
        if (category == FeeCategory.TRANSPORT) return studentRouteRepo.existsByStudentUuidAndActiveTrue(s.getUuid());
        return true;
    }

    private double resolveAmount(FeeItem item, String structureTerm) {
        if ("Per Term".equals(item.getTerm())) {
            Double perTerm = switch (structureTerm) {
                case "Term 1" -> item.getTerm1Amount();
                case "Term 2" -> item.getTerm2Amount();
                case "Term 3" -> item.getTerm3Amount();
                default -> null;
            };
            if (perTerm != null) return perTerm;
        }
        return item.getAmount();
    }

    private List<FeeStructureLine> defaultLines(String term) {
        return feeItemRepo.findAll().stream()
                .map(item -> FeeStructureLine.builder().itemId(item.getId()).enabled(item.isActive()).amount(resolveAmount(item, term)).build())
                .toList();
    }

    private List<FeeStructureLine> mergeWithCurrentItems(List<FeeStructureLine> existing, String term) {
        List<FeeItem> allItems = feeItemRepo.findAll();
        Set<Long> knownIds = existing.stream().map(FeeStructureLine::getItemId).collect(Collectors.toSet());
        List<FeeStructureLine> merged = new ArrayList<>();
        for (FeeStructureLine line : existing) {
            if (allItems.stream().anyMatch(item -> item.getId().equals(line.getItemId()))) merged.add(line);
        }
        for (FeeItem item : allItems) {
            if (!knownIds.contains(item.getId()))
                merged.add(FeeStructureLine.builder().itemId(item.getId()).enabled(false).amount(resolveAmount(item, term)).build());
        }
        return merged;
    }

    private List<FeeStructureLine> computeBaseline(String grade, String term) {
        return feeStructureRepo.findFirstByGradeAndTermAndStatusOrderByReviewedAtDesc(grade, term, FeeStructure.Status.APPROVED)
                .map(fs -> mergeWithCurrentItems(fs.getLines(), term))
                .orElseGet(() -> defaultLines(term));
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
                .dueDate(req.getDueDate())
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
            fs.setDueDate(req.getDueDate());
            fs.setMaker(maker);
            fs.setStatus(FeeStructure.Status.PENDING_APPROVAL);
            fs.setRejectionReason(null);
            fs.setSubmittedAt(now);
            fs.setUpdatedAt(now);
        } else {
            if (feeStructureRepo.countByGradeAndTermAndStatus(req.getGrade(), req.getTerm(), FeeStructure.Status.PENDING_APPROVAL) > 0)
                throw new IllegalArgumentException(req.getGrade() + " · " + req.getTerm() + " already has a fee structure pending approval");
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
                    .dueDate(req.getDueDate())
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
    public FeeStructure approveStructure(UUID uuid, String note) {
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
        addStructureAudit(actor.getUserName(), FeeStructureAudit.Action.APPROVED, fs.getGrade(), fs.getTerm(), fs.getAcademicYear(), note);
        generateCharges(saved);
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
