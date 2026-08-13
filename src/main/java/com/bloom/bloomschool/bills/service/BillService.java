package com.bloom.bloomschool.bills.service;

import com.bloom.bloomschool.bills.dto.BillRequest;
import com.bloom.bloomschool.bills.dto.BillResponse;
import com.bloom.bloomschool.bills.entity.Bill;
import com.bloom.bloomschool.bills.repository.BillRepository;
import com.bloom.bloomschool.setups.service.RefGeneratorService;
import com.bloom.bloomschool.suppliers.entity.Supplier;
import com.bloom.bloomschool.suppliers.repository.SupplierRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BillService {

    private final BillRepository billRepo;
    private final SupplierRepository supplierRepo;
    private final RefGeneratorService refGeneratorService;

    public List<BillResponse> getAll(String search) {
        List<Bill> bills = (search != null && !search.isBlank()) ? billRepo.search(search.trim()) : billRepo.findAll();
        return bills.stream().map(this::toResponse).toList();
    }

    public BillResponse getById(Long id) {
        return toResponse(getEntity(id));
    }

    @Transactional
    public BillResponse create(BillRequest req) {
        Bill bill = build(new Bill(), req);
        bill.setBillNumber(refGeneratorService.generateReference("BILL"));
        return toResponse(billRepo.save(bill));
    }

    @Transactional
    public BillResponse update(Long id, BillRequest req) {
        return toResponse(billRepo.save(build(getEntity(id), req)));
    }

    @Transactional
    public BillResponse markPaid(Long id, String paymentRef) {
        Bill b = getEntity(id);
        if (b.getStatus() == Bill.Status.PAID) throw new IllegalArgumentException("Bill is already marked as paid");
        b.setStatus(Bill.Status.PAID);
        b.setPaidDate(LocalDateTime.now());
        b.setPaymentRef(paymentRef);
        return toResponse(billRepo.save(b));
    }

    @Transactional
    public void softDelete(Long id){
        Bill bill = billRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bill not found, please contact admin"));
        bill.setDeleted(!bill.isDeleted());

        billRepo.save(bill);
    };

    @Transactional
    public void delete(Long id) {
        billRepo.deleteById(getEntity(id).getId());
    }

    private Bill getEntity(Long id) {
        return billRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Bill not found"));
    }

    private Bill build(Bill b, BillRequest req) {
        Supplier supplier = null;
        if (req.getSupplierId() != null) {
            supplier = supplierRepo.findById(req.getSupplierId())
                    .orElseThrow(() -> new EntityNotFoundException("Supplier not found"));
        }
        String supplierName = supplier != null ? supplier.getName() : req.getSupplierName();
        if (supplierName == null || supplierName.isBlank()) {
            throw new IllegalArgumentException("Either supplierId or supplierName is required");
        }

        b.setSupplier(supplier);
        b.setSupplierName(supplierName);
        b.setDescription(req.getDescription());
        b.setAmount(req.getAmount());
        b.setDueDate(req.getDueDate());
        return b;
    }

    private BillResponse toResponse(Bill b) {
        String status = b.getStatus() == Bill.Status.UNPAID && b.getDueDate().isBefore(LocalDate.now())
                ? "OVERDUE" : b.getStatus().name();
        return BillResponse.builder()
                .id(b.getId())
                .uuid(b.getUuid())
                .billNumber(b.getBillNumber())
                .supplierId(b.getSupplier() != null ? b.getSupplier().getId() : null)
                .supplierName(b.getSupplierName())
                .description(b.getDescription())
                .amount(b.getAmount())
                .dueDate(b.getDueDate())
                .paidDate(b.getPaidDate())
                .status(status)
                .build();
    }
}
