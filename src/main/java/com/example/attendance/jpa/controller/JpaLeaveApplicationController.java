package com.example.attendance.jpa.controller;

import com.example.attendance.common.Result;
import com.example.attendance.jpa.entity.LeaveApplication;
import com.example.attendance.jpa.service.JpaLeaveApplicationService;
import org.springframework.data.domain.Page;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/leave")
public class JpaLeaveApplicationController {

    private final JpaLeaveApplicationService leaveApplicationService;

    public JpaLeaveApplicationController(JpaLeaveApplicationService leaveApplicationService) {
        this.leaveApplicationService = leaveApplicationService;
    }

    @PostMapping("/apply")
    public ResponseEntity<Result<LeaveApplication>> applyLeave(@RequestBody LeaveApplication application) {
        return ResponseEntity.ok(leaveApplicationService.applyLeave(application));
    }

    @PostMapping("/approve/{leaveId}")
    public ResponseEntity<Result<Void>> approveLeave(
            @PathVariable Integer leaveId,
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {
        try {
            // 权限验证：只有教师或管理员可以审批请假
            String currentRole = (String) httpRequest.getAttribute("role");
            if (!"teacher".equalsIgnoreCase(currentRole) && !"admin".equalsIgnoreCase(currentRole)) {
                return ResponseEntity.ok(Result.error("权限不足：只有教师或管理员可以审批请假"));
            }
            
            Object approverIdObj = request.get("approverId");
            Integer approverId = approverIdObj instanceof Integer ? (Integer) approverIdObj : 
                                (approverIdObj instanceof String ? Integer.parseInt((String) approverIdObj) : null);
            String status = (String) request.get("status");
            String comment = (String) request.get("comment");
            
            if (approverId == null) {
                return ResponseEntity.ok(Result.error("审批人ID无效"));
            }
            if (status == null || status.isEmpty()) {
                return ResponseEntity.ok(Result.error("状态不能为空"));
            }
            
            return ResponseEntity.ok(leaveApplicationService.approveLeave(leaveId, approverId, status, comment));
        } catch (NumberFormatException e) {
            return ResponseEntity.ok(Result.error("审批人ID格式错误"));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(Result.error(e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<LeaveApplication>> getLeaveApplicationsByUserId(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "0") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return ResponseEntity.ok(leaveApplicationService.getLeaveApplicationsByUserId(userId, pageNum, pageSize));
    }

    @GetMapping("/pending")
    public ResponseEntity<Page<LeaveApplication>> getPendingApplications(
            @RequestParam(defaultValue = "0") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return ResponseEntity.ok(leaveApplicationService.getPendingApplications(pageNum, pageSize));
    }

    @GetMapping("/{leaveId}")
    public ResponseEntity<LeaveApplication> getById(@PathVariable Integer leaveId) {
        LeaveApplication application = leaveApplicationService.getById(leaveId);
        if (application == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(application);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<LeaveApplication>> findByStatus(@PathVariable String status) {
        return ResponseEntity.ok(leaveApplicationService.findByStatus(status));
    }

    @GetMapping("/status/pending")
    public ResponseEntity<List<LeaveApplication>> getPendingApplicationsList() {
        return ResponseEntity.ok(leaveApplicationService.getPendingApplicationsList());
    }

    @GetMapping("/list")
    public ResponseEntity<List<LeaveApplication>> getAllLeaveApplications() {
        return ResponseEntity.ok(leaveApplicationService.getAllLeaveApplications());
    }

    @DeleteMapping("/{leaveId}")
    public ResponseEntity<Result<Void>> deleteLeaveApplication(@PathVariable Integer leaveId) {
        return ResponseEntity.ok(leaveApplicationService.deleteLeaveApplication(leaveId));
    }
}