package com.example.attendance.jpa.service;

import com.example.attendance.common.Result;
import com.example.attendance.jpa.entity.LeaveApplication;
import org.springframework.data.domain.Page;

import java.util.List;

public interface JpaLeaveApplicationService {
    Result<LeaveApplication> applyLeave(LeaveApplication application);

    Result<Void> approveLeave(Integer leaveId, Integer approverId, String status, String comment);

    Page<LeaveApplication> getLeaveApplicationsByUserId(Integer userId, int pageNum, int pageSize);

    Page<LeaveApplication> getPendingApplications(int pageNum, int pageSize);

    LeaveApplication getById(Integer leaveId);

    List<LeaveApplication> findByStatus(String status);

    List<LeaveApplication> getPendingApplicationsList();

    List<LeaveApplication> getAllLeaveApplications();

    Result<Void> deleteLeaveApplication(Integer leaveId);
}