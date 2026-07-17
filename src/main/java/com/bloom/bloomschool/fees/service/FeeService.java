package com.bloom.bloomschool.fees.service;

import com.bloom.bloomschool.fees.dto.FeeItemRequest;
import com.bloom.bloomschool.fees.dto.FeePaymentRequest;
import com.bloom.bloomschool.fees.entity.FeeItem;
import com.bloom.bloomschool.fees.entity.FeePayment;
import com.bloom.bloomschool.fees.repository.FeeItemRepository;
import com.bloom.bloomschool.fees.repository.FeePaymentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeeService {

    private final FeeItemRepository feeItemRepo;
    private final FeePaymentRepository feePaymentRepo;

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
}
