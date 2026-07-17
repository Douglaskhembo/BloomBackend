package com.bloom.bloomschool.staff.service;

import com.bloom.bloomschool.staff.dto.StaffRequest;
import com.bloom.bloomschool.staff.entity.Staff;
import com.bloom.bloomschool.staff.repository.StaffRepository;
import com.bloom.bloomschool.staff.util.StaffType;
import com.bloom.bloomschool.staff.util.Status;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StaffService {

    private final StaffRepository staffRepo;

    public List<Staff> getAll(String search) {
        if (search != null && !search.isBlank()) return staffRepo.search(search.trim());
        return staffRepo.findAll();
    }

    public Staff getById(Long id) {
        return staffRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Staff not found"));
    }

    public Staff getByStaffId(String staffId) {
        return staffRepo.findByStaffId(staffId).orElseThrow(() -> new EntityNotFoundException("Staff not found: " + staffId));
    }

    @Transactional
    public Staff create(StaffRequest req) {
        if (req.getEmail() != null && staffRepo.existsByEmail(req.getEmail()))
            throw new IllegalArgumentException("Email already in use");
        if (req.getIdNumber() != null && staffRepo.existsByIdNumber(req.getIdNumber()))
            throw new IllegalArgumentException("ID number already registered");

        String staffId = generateStaffId(req.getStaffType());
        Staff s = buildStaff(new Staff(), req);
        s.setStaffId(staffId);
        return staffRepo.save(s);
    }

    @Transactional
    public Staff update(Long id, StaffRequest req) {
        Staff s = getById(id);
        return staffRepo.save(buildStaff(s, req));
    }

    @Transactional
    public Staff updateStatus(Long id, Status status) {
        Staff s = getById(id);
        s.setStatus(status);
        return staffRepo.save(s);
    }

    @Transactional
    public void delete(Long id) {
        staffRepo.deleteById(id);
    }

    private String generateStaffId(StaffType type) {
        String prefix = type == StaffType.TEACHING ? "TCH" : "SUP";
        long count = staffRepo.countByStaffType(type);
        return prefix + "-" + String.format("%03d", count + 1);
    }

    private Staff buildStaff(Staff s, StaffRequest req) {
        s.setFirstName(req.getFirstName());
        s.setLastName(req.getLastName());
        s.setGender(req.getGender());
        s.setDateOfBirth(req.getDateOfBirth());
        s.setIdNumber(req.getIdNumber());
        s.setPhone(req.getPhone());
        s.setEmail(req.getEmail());
        s.setAddress(req.getAddress());
        s.setPracticeNumber(req.getPracticeNumber());
        s.setStaffType(req.getStaffType());
        s.setSubject(req.getSubject());
        s.setGrade(req.getGrade());
        s.setQualification(req.getQualification());
        s.setExperience(req.getExperience());
        s.setJoined(req.getJoined());
        s.setEmergencyContactName(req.getEmergencyContactName());
        s.setEmergencyContactPhone(req.getEmergencyContactPhone());
        s.setEmergencyContactRelationship(req.getEmergencyContactRelationship());
        return s;
    }
}
