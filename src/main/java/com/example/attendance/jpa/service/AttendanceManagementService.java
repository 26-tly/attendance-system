package com.example.attendance.jpa.service;

import com.example.attendance.common.Result;
import com.example.attendance.jpa.entity.AttendanceHistory;
import com.example.attendance.jpa.entity.AttendanceLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Map;

public interface AttendanceManagementService {
    
    Result<Map<String, Object>> getAttendanceStatus(Integer courseId, LocalDate date);
    
    Result<Map<String, Object>> makeUpAttendance(Long studentId, Integer courseId, 
                                                LocalDate date, String reason, Integer operatorId);
    
    Result<Map<String, Object>> updateAttendanceStatus(Integer attendanceId, String newStatus,
                                                       String reason, Integer operatorId);
    
    Result<Page<Map<String, Object>>> getAttendanceHistory(Integer courseId, Long studentId,
                                                            Integer userId, String studentNo, String status,
                                                            LocalDate startDate, LocalDate endDate,
                                                            Pageable pageable);
    
    Result<Page<Map<String, Object>>> getAttendanceLogs(Long studentId, Integer courseId,
                                                         Integer operatorId, String actionType,
                                                         Pageable pageable);
    
    Result<Map<String, Object>> getStudentAttendanceSummary(Long studentId, Integer courseId,
                                                            LocalDate startDate, LocalDate endDate);
}
