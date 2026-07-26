package com.bloom.bloomschool.payroll.repository;

import com.bloom.bloomschool.payroll.entity.PayrollRunApproval;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PayrollRunApprovalRepository extends JpaRepository<PayrollRunApproval, Long> {
    List<PayrollRunApproval> findByPayrollRunIdOrderByActedAtAsc(Long payrollRunId);
    List<PayrollRunApproval> findByPayrollRunIdAndStepOrderAndAction(Long payrollRunId, Integer stepOrder, PayrollRunApproval.Action action);
}
