package com.bloom.bloomschool.leave.service;

import com.bloom.bloomschool.leave.dto.LeaveBalanceResponse;
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

import java.time.LocalDate;
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
                .paid(req.isPaid())
                .requiresDocument(req.isRequiresDocument())
                .documentTypes(req.isRequiresDocument() && req.getDocumentTypes() != null ? req.getDocumentTypes() : List.of())
                .build());
    }

    @Transactional
    public LeaveType updateLeaveType(Long id, LeaveTypeRequest req) {
        LeaveType lt = leaveTypeRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Leave type not found"));
        lt.setName(req.getName());
        lt.setMaxDaysPerYear(req.getMaxDaysPerYear());
        lt.setRequiresApproval(req.isRequiresApproval());
        lt.setPaid(req.isPaid());
        lt.setRequiresDocument(req.isRequiresDocument());
        lt.setDocumentTypes(req.isRequiresDocument() && req.getDocumentTypes() != null ? req.getDocumentTypes() : List.of());
        return leaveTypeRepo.save(lt);
    }

    @Transactional
    public void toggleLeaveTypeStatus(Long id) {
        LeaveType lt = leaveTypeRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Leave type not found"));
        lt.setActive(!lt.isActive());
        leaveTypeRepo.save(lt);
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
                .documentName(req.getDocumentName())
                .documentType(req.getDocumentType())
                .status(LeaveRequest.Status.PENDING)
                .build();
        return leaveRequestRepo.save(lr);
    }

    /** Remaining balance per active leave type, for the current calendar year, based on approved requests. */
    public List<LeaveBalanceResponse> getBalances(String staffId) {
        int currentYear = LocalDate.now().getYear();
        List<LeaveRequest> approved = leaveRequestRepo.findByStaffIdAndStatus(staffId, LeaveRequest.Status.APPROVED);

        return leaveTypeRepo.findAll().stream()
                .filter(LeaveType::isActive)
                .map(lt -> {
                    int used = approved.stream()
                            .filter(r -> r.getLeaveType().getId().equals(lt.getId()))
                            .filter(r -> r.getFromDate().getYear() == currentYear)
                            .mapToInt(LeaveRequest::getDays)
                            .sum();
                    return LeaveBalanceResponse.builder()
                            .leaveTypeId(lt.getId())
                            .leaveTypeUuid(lt.getUuid())
                            .leaveTypeName(lt.getName())
                            .maxDaysPerYear(lt.getMaxDaysPerYear())
                            .usedDays(used)
                            .remainingDays(Math.max(0, lt.getMaxDaysPerYear() - used))
                            .build();
                })
                .toList();
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
