package com.bloom.bloomschool.suppliers.service;

import com.bloom.bloomschool.suppliers.dto.SupplierRequest;
import com.bloom.bloomschool.suppliers.entity.Supplier;
import com.bloom.bloomschool.suppliers.repository.SupplierRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SupplierService {

    private final SupplierRepository supplierRepo;

    public List<Supplier> getAll(String search) {
        if (search != null && !search.isBlank()) return supplierRepo.search(search.trim());
        return supplierRepo.findAll();
    }

    public Supplier getById(Long id) {
        return supplierRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Supplier not found"));
    }

    @Transactional
    public Supplier create(SupplierRequest req) {
        return supplierRepo.save(build(new Supplier(), req));
    }

    @Transactional
    public Supplier update(Long id, SupplierRequest req) {
        return supplierRepo.save(build(getById(id), req));
    }

    @Transactional
    public Supplier toggleStatus(Long id) {
        Supplier s = getById(id);
        s.setStatus(s.getStatus() == Supplier.Status.ACTIVE ? Supplier.Status.INACTIVE : Supplier.Status.ACTIVE);
        return supplierRepo.save(s);
    }

    @Transactional
    public void delete(Long id) {
        supplierRepo.deleteById(id);
    }

    private Supplier build(Supplier s, SupplierRequest req) {
        s.setName(req.getName());
        s.setContactPerson(req.getContactPerson());
        s.setPhone(req.getPhone());
        s.setEmail(req.getEmail());
        s.setAddress(req.getAddress());
        s.setCategory(req.getCategory());
        s.setKraPin(req.getKraPin());
        return s;
    }
}
