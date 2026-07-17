package com.bloom.bloomschool.leave.service;

import com.bloom.bloomschool.leave.dto.LeaveRequestDto;
import com.bloom.bloomschool.leave.dto.LeaveTypeRequest;
import com.bloom.bloomschool.leave.entity.LeaveRequest;
import com.bloom.bloomschool.leave.entity.LeaveType;
import com.bloom.bloomschool.leave.repository.LeaveRequestRepository;
import com.bloom.bloomschool.leave.repository.LeaveTypeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LeaveService {

    private final LeaveTypeRepository leaveTypeRepo;
    private final LeaveRequestRepository leaveRequestRepo;

    // ── Leave Types ───────────────────────────────────────────────────────────

    public List<LeaveType> getAllLeaveTypes() {
        return leaveTypeRepo.findAll();
    }

    @Transactional
    public LeaveType createLeaveType(LeaveTypeRequest req) {
        if (leaveTypeRepo.existsByName(req.getName()))
            throw new IllegalArgumentException("Leave type '" + req.getName() + "' already exists");
        return leaveTypeRepo.save(LeaveType.builder()
                .name(req.getName())
                .maxDaysPerYear(req.getMaxDaysPerYear())
                .requiresApproval(req.isRequiresApproval())
                .build());
    }

    @Transactional
    public LeaveType updateLeaveType(Long id, LeaveTypeRequest req) {
        LeaveType lt = leaveTypeRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Leave type not found"));
        lt.setName(req.getName());
        lt.setMaxDaysPerYear(req.getMaxDaysPerYear());
        lt.setRequiresApproval(req.isRequiresApproval());
        return leaveTypeRepo.save(lt);
    }

    @Transactional
    public void deleteLeaveType(Long id) {
        leaveTypeRepo.deleteById(id);
    }

    // ── Leave Requests ────────────────────────────────────────────────────────

    public List<LeaveRequest> getAllRequests() {
        return leaveRequestRepo.findAll();
    }

    public List<LeaveRequest> getRequestsByStaff(String staffId) {
        return leaveRequestRepo.findByStaffId(staffId);
    }

    @Transactional
    public LeaveRequest createRequest(LeaveRequestDto req) {
        LeaveType lt = leaveTypeRepo.findById(req.getLeaveTypeId())
                .orElseThrow(() -> new EntityNotFoundException("Leave type not found"));
        long days = ChronoUnit.DAYS.between(req.getFromDate(), req.getToDate()) + 1;
        long count = leaveRequestRepo.count();
        String leaveId = "LV-" + String.format("%03d", count + 1);

        LeaveRequest lr = LeaveRequest.builder()
                .leaveId(leaveId)
                .staffId(req.getStaffId())
                .staffName(req.getStaffName())
                .leaveType(lt)
                .fromDate(req.getFromDate())
                .toDate(req.getToDate())
                .days((int) days)
                .reason(req.getReason())
                .status(LeaveRequest.Status.PENDING)
                .build();
        return leaveRequestRepo.save(lr);
    }

    @Transactional
    public LeaveRequest reviewRequest(Long id, LeaveRequest.Status status, String note) {
        LeaveRequest lr = leaveRequestRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Leave request not found"));
        if (lr.getStatus() != LeaveRequest.Status.PENDING)
            throw new IllegalArgumentException("Only pending requests can be reviewed");
        lr.setStatus(status);
        lr.setReviewNote(note);
        return leaveRequestRepo.save(lr);
    }

    @Transactional
    public void deleteRequest(Long id) {
        leaveRequestRepo.deleteById(id);
    }
}
