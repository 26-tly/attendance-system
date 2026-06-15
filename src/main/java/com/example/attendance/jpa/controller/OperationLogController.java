package com.example.attendance.jpa.controller;

import com.example.attendance.jpa.entity.OperationLog;
import com.example.attendance.jpa.service.OperationLogService;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/logs")
public class OperationLogController {

    private final OperationLogService operationLogService;

    public OperationLogController(OperationLogService operationLogService) {
        this.operationLogService = operationLogService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<OperationLog>> getLogsByUserId(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "0") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return ResponseEntity.ok(operationLogService.getLogsByUserId(userId, pageNum, pageSize));
    }

    @GetMapping("/module/{module}")
    public ResponseEntity<Page<OperationLog>> getLogsByModule(
            @PathVariable String module,
            @RequestParam(defaultValue = "0") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return ResponseEntity.ok(operationLogService.getLogsByModule(module, pageNum, pageSize));
    }

    @GetMapping("/time-range")
    public ResponseEntity<Page<OperationLog>> getLogsByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(defaultValue = "0") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return ResponseEntity.ok(operationLogService.getLogsByTimeRange(startTime, endTime, pageNum, pageSize));
    }

    @GetMapping("/recent")
    public ResponseEntity<List<OperationLog>> getRecentLogs() {
        return ResponseEntity.ok(operationLogService.getRecentLogs());
    }
}