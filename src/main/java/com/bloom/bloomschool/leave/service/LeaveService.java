package com.bloom.bloomschool.leave.service;

import com.bloom.bloomschool.common.util.LeaveDayCounter;
import com.bloom.bloomschool.holidays.service.HolidayService;
import com.bloom.bloomschool.leave.dto.LeaveBalanceResponse;
import com.bloom.bloomschool.leave.dto.LeaveRequestDto;
import com.bloom.bloomschool.leave.dto.LeaveTypeRequest;
import com.bloom.bloomschool.leave.entity.LeaveRequest;
import com.bloom.bloomschool.leave.entity.LeaveType;
import com.bloom.bloomschool.leave.repository.LeaveRequestRepository;
import com.bloom.bloomschool.leave.repository.LeaveTypeRepository;
import com.bloom.bloomschool.staff.entity.Staff;
import com.bloom.bloomschool.staff.repository.StaffRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LeaveService {

    private final LeaveTypeRepository leaveTypeRepo;
    private final LeaveRequestRepository leaveRequestRepo;
    private final StaffRepository staffRepo;
    private final HolidayService holidayService;

    // ── Leave Types ───────────────────────────────────────────────────────────

    public List<LeaveType> getAllLeaveTypes() {
        return leaveTypeRepo.findAll();
    }

    @Transactional
    public LeaveType createLeaveType(LeaveTypeRequest req) {
        if (leaveTypeRepo.existsByName(req.getName()))
            throw new IllegalArgumentException("Leave type '" + req.getName() + "' already exists");
        validateCarryForward(req);
        return leaveTypeRepo.save(LeaveType.builder()
                .name(req.getName())
                .maxDaysPerYear(req.getMaxDaysPerYear())
                .requiresApproval(req.isRequiresApproval())
                .paid(req.isPaid())
                .requiresDocument(req.isRequiresDocument())
                .documentTypes(req.isRequiresDocument() && req.getDocumentTypes() != null ? new ArrayList<>(req.getDocumentTypes()) : new ArrayList<>())
                .carryForwardAllowed(req.isCarryForwardAllowed())
                .maxCarryForwardDays(req.getMaxCarryForwardDays())
                .weekendPolicy(req.getWeekendPolicy())
                .countPublicHolidays(req.isCountPublicHolidays())
                .build());
    }

    @Transactional
    public LeaveType updateLeaveType(Long id, LeaveTypeRequest req) {
        LeaveType lt = leaveTypeRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Leave type not found"));
        validateCarryForward(req);
        lt.setName(req.getName());
        lt.setMaxDaysPerYear(req.getMaxDaysPerYear());
        lt.setRequiresApproval(req.isRequiresApproval());
        lt.setPaid(req.isPaid());
        lt.setRequiresDocument(req.isRequiresDocument());
        lt.setDocumentTypes(req.isRequiresDocument() && req.getDocumentTypes() != null ? new ArrayList<>(req.getDocumentTypes()) : new ArrayList<>());
        lt.setCarryForwardAllowed(req.isCarryForwardAllowed());
        lt.setMaxCarryForwardDays(req.getMaxCarryForwardDays());
        lt.setWeekendPolicy(req.getWeekendPolicy());
        lt.setCountPublicHolidays(req.isCountPublicHolidays());
        return leaveTypeRepo.save(lt);
    }

    private void validateCarryForward(LeaveTypeRequest req) {
        if (req.isCarryForwardAllowed() && req.getMaxCarryForwardDays() > req.getMaxDaysPerYear())
            throw new IllegalArgumentException("Max carryforward days cannot exceed the annual entitlement");
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
        double days = LeaveDayCounter.countLeaveDays(req.getFromDate(), req.getToDate(),
                lt.getWeekendPolicy(), !lt.isCountPublicHolidays(), holidayService.getActiveHolidayDates());
        if (days <= 0)
            throw new IllegalArgumentException("Selected date range contains no countable leave days for this leave type");
        long count = leaveRequestRepo.count();
        String leaveId = "LV-" + String.format("%03d", count + 1);

        LeaveRequest lr = LeaveRequest.builder()
                .leaveId(leaveId)
                .staffId(req.getStaffId())
                .staffName(req.getStaffName())
                .leaveType(lt)
                .fromDate(req.getFromDate())
                .toDate(req.getToDate())
                .days(days)
                .reason(req.getReason())
                .documentName(req.getDocumentName())
                .documentType(req.getDocumentType())
                .status(LeaveRequest.Status.PENDING)
                .build();
        return leaveRequestRepo.save(lr);
    }

    public List<LeaveBalanceResponse> getBalances(String staffId) {
        return getBalances(staffId, LocalDate.now().getYear());
    }

    public List<LeaveBalanceResponse> getBalances(String staffId, int year) {
        LocalDate joined = staffRepo.findByStaffId(staffId).map(Staff::getJoined).orElse(null);
        List<LeaveRequest> approved = leaveRequestRepo.findByStaffIdAndStatus(staffId, LeaveRequest.Status.APPROVED);

        return leaveTypeRepo.findAll().stream()
                .filter(LeaveType::isActive)
                .map(lt -> buildBalance(lt, joined, approved, year))
                .toList();
    }

    private LeaveBalanceResponse buildBalance(LeaveType lt, LocalDate joined, List<LeaveRequest> approved, int year) {
        double prorated = proratedEntitlement(lt.getMaxDaysPerYear(), joined, year);
        double carried = 0;
        if (lt.isCarryForwardAllowed()) {
            double prevProrated = proratedEntitlement(lt.getMaxDaysPerYear(), joined, year - 1);
            double prevUsed = usedDaysInYear(approved, lt.getId(), year - 1);
            carried = Math.min(lt.getMaxCarryForwardDays(), Math.max(0, prevProrated - prevUsed));
        }
        double totalAvailable = prorated + carried;
        double used = usedDaysInYear(approved, lt.getId(), year);

        return LeaveBalanceResponse.builder()
                .leaveTypeId(lt.getId())
                .leaveTypeUuid(lt.getUuid())
                .leaveTypeName(lt.getName())
                .maxDaysPerYear(lt.getMaxDaysPerYear())
                .year(year)
                .proratedEntitlement(prorated)
                .carryForwardAllowed(lt.isCarryForwardAllowed())
                .carriedForwardDays(carried)
                .totalAvailableDays(totalAvailable)
                .usedDays(used)
                .remainingDays(Math.max(0, totalAvailable - used))
                .build();
    }

    private double usedDaysInYear(List<LeaveRequest> approved, Long leaveTypeId, int year) {
        return approved.stream()
                .filter(r -> r.getLeaveType().getId().equals(leaveTypeId))
                .filter(r -> r.getFromDate().getYear() == year)
                .mapToDouble(LeaveRequest::getDays)
                .sum();
    }

    /**
     * Pro-rata (by calendar day, leap-year-safe) entitlement for a leave year, based on join
     * date: full entitlement if hired before that year (or join date unknown — legacy records
     * default to full entitlement rather than being silently punished), zero if not yet hired
     * during that year, otherwise scaled by the fraction of the year actually employed.
     */
    private double proratedEntitlement(int maxDaysPerYear, LocalDate joined, int year) {
        if (joined == null || joined.getYear() < year) return maxDaysPerYear;
        if (joined.getYear() > year) return 0;
        LocalDate yearEnd = LocalDate.of(year, 12, 31);
        long daysEmployed = ChronoUnit.DAYS.between(joined, yearEnd) + 1;
        int totalDaysInYear = Year.isLeap(year) ? 366 : 365;
        return Math.round(maxDaysPerYear * (double) daysEmployed / totalDaysInYear);
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
