package com.example.attendance.jpa.service;

import com.example.attendance.jpa.entity.OperationLog;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public interface OperationLogService {
    void saveLog(Integer userId, String username, String operationType, String module, String description, String ipAddress, Boolean success, String errorMessage);

    Page<OperationLog> getLogsByUserId(Integer userId, int pageNum, int pageSize);

    Page<OperationLog> getLogsByModule(String module, int pageNum, int pageSize);

    Page<OperationLog> getLogsByTimeRange(LocalDateTime startTime, LocalDateTime endTime, int pageNum, int pageSize);

    List<OperationLog> getRecentLogs();
}