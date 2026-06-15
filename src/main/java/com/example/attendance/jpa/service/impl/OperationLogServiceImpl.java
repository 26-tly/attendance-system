package com.example.attendance.jpa.service.impl;

import com.example.attendance.jpa.entity.OperationLog;
import com.example.attendance.jpa.repository.OperationLogRepository;
import com.example.attendance.jpa.service.OperationLogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OperationLogServiceImpl implements OperationLogService {

    private final OperationLogRepository operationLogRepository;

    public OperationLogServiceImpl(OperationLogRepository operationLogRepository) {
        this.operationLogRepository = operationLogRepository;
    }

    @Override
    @Async
    public void saveLog(Integer userId, String username, String operationType, String module, String description, String ipAddress, Boolean success, String errorMessage) {
        OperationLog log = OperationLog.builder()
                .userId(userId)
                .username(username)
                .operationType(operationType)
                .module(module)
                .description(description)
                .ipAddress(ipAddress)
                .success(success)
                .errorMessage(errorMessage)
                .build();
        operationLogRepository.save(log);
    }

    @Override
    public Page<OperationLog> getLogsByUserId(Integer userId, int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by(Sort.Direction.DESC, "operationTime"));
        return operationLogRepository.findByUserId(userId, pageable);
    }

    @Override
    public Page<OperationLog> getLogsByModule(String module, int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by(Sort.Direction.DESC, "operationTime"));
        return operationLogRepository.findByModule(module, pageable);
    }

    @Override
    public Page<OperationLog> getLogsByTimeRange(LocalDateTime startTime, LocalDateTime endTime, int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by(Sort.Direction.DESC, "operationTime"));
        return operationLogRepository.findByOperationTimeBetween(startTime, endTime, pageable);
    }

    @Override
    public List<OperationLog> getRecentLogs() {
        return operationLogRepository.findTop10ByOrderByOperationTimeDesc();
    }
}