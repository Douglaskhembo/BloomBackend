package com.bloom.bloomschool.leave.repository;

import com.bloom.bloomschool.leave.entity.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByStaffId(String staffId);
    List<LeaveRequest> findByStaffIdAndStatus(String staffId, LeaveRequest.Status status);
    List<LeaveRequest> findByStatus(LeaveRequest.Status status);
    long countByStatus(LeaveRequest.Status status);
    long count();
}
