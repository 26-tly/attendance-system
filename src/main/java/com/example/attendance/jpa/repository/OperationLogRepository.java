package com.example.attendance.jpa.repository;

import com.example.attendance.jpa.entity.OperationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OperationLogRepository extends JpaRepository<OperationLog, Long> {
    Page<OperationLog> findByUserId(Integer userId, Pageable pageable);

    Page<OperationLog> findByModule(String module, Pageable pageable);

    Page<OperationLog> findByOperationTimeBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);

    Page<OperationLog> findByUserIdAndOperationTimeBetween(Integer userId, LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);

    List<OperationLog> findTop10ByOrderByOperationTimeDesc();
}