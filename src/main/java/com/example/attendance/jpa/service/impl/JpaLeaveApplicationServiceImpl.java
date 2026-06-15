package com.example.attendance.jpa.service.impl;

import com.example.attendance.common.Result;
import com.example.attendance.jpa.entity.LeaveApplication;
import com.example.attendance.jpa.repository.LeaveApplicationRepository;
import com.example.attendance.jpa.service.JpaLeaveApplicationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class JpaLeaveApplicationServiceImpl implements JpaLeaveApplicationService {

    private final LeaveApplicationRepository leaveApplicationRepository;

    public JpaLeaveApplicationServiceImpl(LeaveApplicationRepository leaveApplicationRepository) {
        this.leaveApplicationRepository = leaveApplicationRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<LeaveApplication> applyLeave(LeaveApplication application) {
        if (application.getUserId() == null || application.getUserId() <= 0) {
            return Result.error("用户ID无效");
        }
        if (application.getCourseId() == null || application.getCourseId() <= 0) {
            return Result.error("课程ID无效");
        }
        if (application.getLeaveType() == null || application.getLeaveType().isEmpty()) {
            return Result.error("请选择请假类型");
        }
        if (application.getStartDate() == null) {
            return Result.error("请选择开始日期");
        }
        if (application.getEndDate() == null) {
            return Result.error("请选择结束日期");
        }
        if (application.getStartDate().isAfter(application.getEndDate())) {
            return Result.error("开始日期不能晚于结束日期");
        }
        if (application.getReason() == null || application.getReason().trim().isEmpty()) {
            return Result.error("请填写请假原因");
        }

        application.setStatus("pending");
        LeaveApplication saved = leaveApplicationRepository.save(application);
        return Result.success(saved);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> approveLeave(Integer leaveId, Integer approverId, String status, String comment) {
        LeaveApplication application = leaveApplicationRepository.findById(leaveId).orElse(null);
        
        if (application == null) {
            return Result.error("请假申请不存在");
        }

        if (!"pending".equals(application.getStatus())) {
            return Result.error("申请已处理，无法重复操作");
        }

        application.setStatus(status);
        application.setApproverId(approverId);
        application.setApproveTime(LocalDateTime.now());
        application.setApproveComment(comment);
        leaveApplicationRepository.save(application);

        return Result.success();
    }

    @Override
    public Page<LeaveApplication> getLeaveApplicationsByUserId(Integer userId, int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by(Sort.Direction.DESC, "applyTime"));
        return leaveApplicationRepository.findByUserId(userId, pageable);
    }

    @Override
    public Page<LeaveApplication> getPendingApplications(int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by(Sort.Direction.DESC, "applyTime"));
        return leaveApplicationRepository.findByStatus("pending", pageable);
    }

    @Override
    public LeaveApplication getById(Integer leaveId) {
        return leaveApplicationRepository.findById(leaveId).orElse(null);
    }

    @Override
    public List<LeaveApplication> findByStatus(String status) {
        return leaveApplicationRepository.findByStatus(status);
    }

    @Override
    public List<LeaveApplication> getPendingApplicationsList() {
        return leaveApplicationRepository.findByStatusOrderByApplyTimeDesc("pending");
    }

    @Override
    public List<LeaveApplication> getAllLeaveApplications() {
        return leaveApplicationRepository.findAll(Sort.by(Sort.Direction.DESC, "applyTime"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> deleteLeaveApplication(Integer leaveId) {
        if (!leaveApplicationRepository.existsById(leaveId)) {
            return Result.error("请假申请不存在");
        }
        leaveApplicationRepository.deleteById(leaveId);
        return Result.success();
    }
}