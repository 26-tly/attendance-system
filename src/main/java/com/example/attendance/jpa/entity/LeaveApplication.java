package com.example.attendance.jpa.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "leave_application")
public class LeaveApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "leave_id")
    private Integer leaveId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "course_id", nullable = false)
    private Integer courseId;

    @Column(name = "leave_type", nullable = false, length = 20)
    private String leaveType;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "reason", length = 500)
    private String reason;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "apply_time", updatable = false)
    private LocalDateTime applyTime;

    @Column(name = "approve_time")
    private LocalDateTime approveTime;

    @Column(name = "approve_comment", length = 500)
    private String approveComment;

    @Column(name = "approver_id")
    private Integer approverId;

    public LeaveApplication() {}

    public LeaveApplication(Integer leaveId, Integer userId, Integer courseId, String leaveType,
                           LocalDate startDate, LocalDate endDate, String reason, String status,
                           LocalDateTime applyTime, LocalDateTime approveTime, String approveComment, Integer approverId) {
        this.leaveId = leaveId;
        this.userId = userId;
        this.courseId = courseId;
        this.leaveType = leaveType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.status = status;
        this.applyTime = applyTime;
        this.approveTime = approveTime;
        this.approveComment = approveComment;
        this.approverId = approverId;
    }

    @PrePersist
    void prePersist() {
        this.applyTime = LocalDateTime.now();
        if (this.status == null) {
            this.status = "pending";
        }
    }

    public Integer getLeaveId() {
        return leaveId;
    }

    public void setLeaveId(Integer leaveId) {
        this.leaveId = leaveId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(LocalDateTime applyTime) {
        this.applyTime = applyTime;
    }

    public LocalDateTime getApproveTime() {
        return approveTime;
    }

    public void setApproveTime(LocalDateTime approveTime) {
        this.approveTime = approveTime;
    }

    public String getApproveComment() {
        return approveComment;
    }

    public void setApproveComment(String approveComment) {
        this.approveComment = approveComment;
    }

    public Integer getApproverId() {
        return approverId;
    }

    public void setApproverId(Integer approverId) {
        this.approverId = approverId;
    }

    public static LeaveApplicationBuilder builder() {
        return new LeaveApplicationBuilder();
    }

    public static class LeaveApplicationBuilder {
        private Integer leaveId;
        private Integer userId;
        private Integer courseId;
        private String leaveType;
        private LocalDate startDate;
        private LocalDate endDate;
        private String reason;
        private String status;
        private LocalDateTime applyTime;
        private LocalDateTime approveTime;
        private String approveComment;
        private Integer approverId;

        public LeaveApplicationBuilder leaveId(Integer leaveId) {
            this.leaveId = leaveId;
            return this;
        }

        public LeaveApplicationBuilder userId(Integer userId) {
            this.userId = userId;
            return this;
        }

        public LeaveApplicationBuilder courseId(Integer courseId) {
            this.courseId = courseId;
            return this;
        }

        public LeaveApplicationBuilder leaveType(String leaveType) {
            this.leaveType = leaveType;
            return this;
        }

        public LeaveApplicationBuilder startDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        public LeaveApplicationBuilder endDate(LocalDate endDate) {
            this.endDate = endDate;
            return this;
        }

        public LeaveApplicationBuilder reason(String reason) {
            this.reason = reason;
            return this;
        }

        public LeaveApplicationBuilder status(String status) {
            this.status = status;
            return this;
        }

        public LeaveApplicationBuilder applyTime(LocalDateTime applyTime) {
            this.applyTime = applyTime;
            return this;
        }

        public LeaveApplicationBuilder approveTime(LocalDateTime approveTime) {
            this.approveTime = approveTime;
            return this;
        }

        public LeaveApplicationBuilder approveComment(String approveComment) {
            this.approveComment = approveComment;
            return this;
        }

        public LeaveApplicationBuilder approverId(Integer approverId) {
            this.approverId = approverId;
            return this;
        }

        public LeaveApplication build() {
            return new LeaveApplication(leaveId, userId, courseId, leaveType, startDate, endDate, 
                                       reason, status, applyTime, approveTime, approveComment, approverId);
        }
    }
}