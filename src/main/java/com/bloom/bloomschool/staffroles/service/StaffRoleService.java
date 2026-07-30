package com.bloom.bloomschool.staffroles.service;

import com.bloom.bloomschool.staffroles.dto.StaffRoleRequest;
import com.bloom.bloomschool.staffroles.entity.StaffRole;
import com.bloom.bloomschool.staffroles.repository.StaffRoleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StaffRoleService {

    private final StaffRoleRepository staffRoleRepo;

    public List<StaffRole> getAll() {
        return staffRoleRepo.findAll();
    }

    @Transactional
    public StaffRole create(StaffRoleRequest req) {
        if (staffRoleRepo.existsByName(req.getName()))
            throw new IllegalArgumentException("Staff role '" + req.getName() + "' already exists");
        return staffRoleRepo.save(StaffRole.builder()
                .name(req.getName())
                .description(req.getDescription())
                .active(req.isActive())
                .build());
    }

    @Transactional
    public StaffRole update(Long id, StaffRoleRequest req) {
        StaffRole r = staffRoleRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Staff role not found"));
        r.setName(req.getName());
        r.setDescription(req.getDescription());
        r.setActive(req.isActive());
        return staffRoleRepo.save(r);
    }

    @Transactional
    public StaffRole toggle(Long id) {
        StaffRole r = staffRoleRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Staff role not found"));
        r.setActive(!r.isActive());
        return staffRoleRepo.save(r);
    }

    @Transactional
    public void delete(Long id) {
        staffRoleRepo.deleteById(id);
    }
}
