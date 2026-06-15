package com.example.attendance.jpa.service.impl;

import com.example.attendance.common.Result;
import com.example.attendance.jpa.entity.*;
import com.example.attendance.jpa.repository.*;
import com.example.attendance.jpa.service.AttendanceManagementService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AttendanceManagementServiceImpl implements AttendanceManagementService {

    private final AttendanceHistoryRepository historyRepository;
    private final AttendanceLogRepository logRepository;
    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public AttendanceManagementServiceImpl(AttendanceHistoryRepository historyRepository,
                                          AttendanceLogRepository logRepository,
                                          AttendanceRepository attendanceRepository,
                                          StudentRepository studentRepository,
                                          CourseRepository courseRepository,
                                          UserRepository userRepository) {
        this.historyRepository = historyRepository;
        this.logRepository = logRepository;
        this.attendanceRepository = attendanceRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Result<Map<String, Object>> getAttendanceStatus(Integer courseId, LocalDate date) {
        if (courseId == null || date == null) {
            return Result.error("课程ID和日期不能为空");
        }

        List<AttendanceHistory> histories = historyRepository.findByCourseIdAndAttendanceDate(courseId, date);
        
        long totalCount = histories.size();
        long presentCount = histories.stream().filter(h -> "present".equals(h.getStatus())).count();
        long absentCount = histories.stream().filter(h -> "absent".equals(h.getStatus())).count();
        long lateCount = histories.stream().filter(h -> "late".equals(h.getStatus())).count();
        long leaveCount = histories.stream().filter(h -> "leave".equals(h.getStatus())).count();

        List<Map<String, Object>> studentList = histories.stream()
                .map(history -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("attendanceId", history.getId());
                    map.put("studentId", history.getStudentId());
                    map.put("userId", history.getUserId());
                    map.put("status", history.getStatus());
                    map.put("checkinTime", history.getCheckinTime());
                    map.put("isMakesUp", history.getIsMakesUp());
                    map.put("makesUpReason", history.getMakesUpReason());
                    
                    studentRepository.findById(history.getStudentId()).ifPresent(student -> {
                        map.put("studentNo", student.getStudentNo());
                        map.put("studentName", student.getName());
                    });
                    
                    return map;
                })
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("courseId", courseId);
        result.put("attendanceDate", date);
        result.put("totalCount", totalCount);
        result.put("presentCount", presentCount);
        result.put("absentCount", absentCount);
        result.put("lateCount", lateCount);
        result.put("leaveCount", leaveCount);
        result.put("studentList", studentList);

        return Result.success(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Map<String, Object>> makeUpAttendance(Long studentId, Integer courseId,
                                                         LocalDate date, String reason, Integer operatorId) {
        if (studentId == null || courseId == null || date == null) {
            return Result.error("学生ID、课程ID和日期不能为空");
        }

        Optional<Student> studentOpt = studentRepository.findById(studentId);
        if (studentOpt.isEmpty()) {
            return Result.error("学生不存在");
        }

        Student student = studentOpt.get();
        
        Optional<AttendanceHistory> existingOpt = historyRepository
                .findByUserIdAndCourseIdAndAttendanceDate(student.getId().intValue(), courseId, date);
        
        AttendanceHistory history;
        String oldStatus = null;
        
        if (existingOpt.isPresent()) {
            history = existingOpt.get();
            oldStatus = history.getStatus();
            history.setStatus("present");
            history.setIsMakesUp(true);
            history.setMakesUpReason(reason);
            history.setMakesUpTime(LocalDateTime.now());
            history.setMakesUpOperator(operatorId);
        } else {
            history = new AttendanceHistory();
            history.setCourseId(courseId);
            history.setStudentId(studentId);
            history.setUserId(student.getId().intValue());
            history.setAttendanceDate(date);
            history.setStatus("present");
            history.setCheckinTime(LocalDateTime.now());
            history.setIsMakesUp(true);
            history.setMakesUpReason(reason);
            history.setMakesUpTime(LocalDateTime.now());
            history.setMakesUpOperator(operatorId);
        }
        
        historyRepository.save(history);
        
        AttendanceLog log = new AttendanceLog(
                history.getId().intValue(),
                studentId,
                courseId,
                operatorId,
                getOperatorName(operatorId),
                "makes_up",
                oldStatus,
                "present",
                reason
        );
        logRepository.save(log);
        
        Map<String, Object> result = new HashMap<>();
        result.put("attendanceId", history.getId());
        result.put("status", "present");
        result.put("makesUpTime", history.getMakesUpTime());
        
        return Result.success(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Map<String, Object>> updateAttendanceStatus(Integer attendanceId, String newStatus,
                                                               String reason, Integer operatorId) {
        if (attendanceId == null || newStatus == null) {
            return Result.error("考勤ID和新状态不能为空");
        }

        Optional<AttendanceHistory> historyOpt = historyRepository.findById(attendanceId.longValue());
        if (historyOpt.isEmpty()) {
            return Result.error("考勤记录不存在");
        }

        AttendanceHistory history = historyOpt.get();
        String oldStatus = history.getStatus();
        
        history.setStatus(newStatus);
        historyRepository.save(history);
        
        AttendanceLog log = new AttendanceLog(
                attendanceId,
                history.getStudentId(),
                history.getCourseId(),
                operatorId,
                getOperatorName(operatorId),
                "status_change",
                oldStatus,
                newStatus,
                reason
        );
        logRepository.save(log);
        
        Map<String, Object> result = new HashMap<>();
        result.put("attendanceId", history.getId());
        result.put("oldStatus", oldStatus);
        result.put("newStatus", newStatus);
        
        return Result.success(result);
    }

    @Override
    public Result<Page<Map<String, Object>>> getAttendanceHistory(Integer courseId, Long studentId,
                                                                  Integer userId, String studentNo, String status,
                                                                  LocalDate startDate, LocalDate endDate,
                                                                  Pageable pageable) {
        Long targetStudentId = studentId;
        
        if (studentNo != null && !studentNo.trim().isEmpty()) {
            List<Student> students = studentRepository.findByStudentNo(studentNo.trim());
            if (students != null && !students.isEmpty()) {
                targetStudentId = students.get(0).getId();
            }
        }
        
        Page<AttendanceHistory> histories = historyRepository.findAttendanceHistoryWithConditions(
                courseId, targetStudentId, userId, status, startDate, endDate, pageable);

        Page<Map<String, Object>> result = histories.map(history -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", history.getId());
            map.put("courseId", history.getCourseId());
            map.put("studentId", history.getStudentId());
            map.put("userId", history.getUserId());
            map.put("attendanceDate", history.getAttendanceDate());
            map.put("status", history.getStatus());
            map.put("sessionCode", history.getSessionCode());
            map.put("checkinTime", history.getCheckinTime());
            map.put("isMakesUp", history.getIsMakesUp());
            map.put("makesUpReason", history.getMakesUpReason());
            map.put("makesUpTime", history.getMakesUpTime());
            
            studentRepository.findById(history.getStudentId()).ifPresent(student -> {
                map.put("studentNo", student.getStudentNo());
                map.put("studentName", student.getName());
            });
            
            courseRepository.findById(history.getCourseId()).ifPresent(course -> {
                map.put("courseName", course.getCourseName());
            });
            
            return map;
        });

        return Result.success(result);
    }

    @Override
    public Result<Page<Map<String, Object>>> getAttendanceLogs(Long studentId, Integer courseId,
                                                                Integer operatorId, String actionType,
                                                                Pageable pageable) {
        Page<AttendanceLog> logs = logRepository.findAttendanceLogWithConditions(
                studentId, courseId, operatorId, actionType, null, null, pageable);

        Page<Map<String, Object>> result = logs.map(log -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", log.getId());
            map.put("attendanceId", log.getAttendanceId());
            map.put("studentId", log.getStudentId());
            map.put("courseId", log.getCourseId());
            map.put("operatorId", log.getOperatorId());
            map.put("operatorName", log.getOperatorName());
            map.put("actionType", log.getActionType());
            map.put("oldStatus", log.getOldStatus());
            map.put("newStatus", log.getNewStatus());
            map.put("reason", log.getReason());
            map.put("operationTime", log.getOperationTime());
            
            studentRepository.findById(log.getStudentId()).ifPresent(student -> {
                map.put("studentNo", student.getStudentNo());
                map.put("studentName", student.getName());
            });
            
            courseRepository.findById(log.getCourseId()).ifPresent(course -> {
                map.put("courseName", course.getCourseName());
            });
            
            return map;
        });

        return Result.success(result);
    }

    @Override
    public Result<Map<String, Object>> getStudentAttendanceSummary(Long studentId, Integer courseId,
                                                                   LocalDate startDate, LocalDate endDate) {
        List<AttendanceHistory> histories = historyRepository
                .findAttendanceHistoryWithConditions(courseId, studentId, null, null, startDate, endDate, Pageable.unpaged())
                .getContent();

        long totalCount = histories.size();
        long presentCount = histories.stream().filter(h -> "present".equals(h.getStatus())).count();
        long absentCount = histories.stream().filter(h -> "absent".equals(h.getStatus())).count();
        long lateCount = histories.stream().filter(h -> "late".equals(h.getStatus())).count();
        long leaveCount = histories.stream().filter(h -> "leave".equals(h.getStatus())).count();
        long makesUpCount = histories.stream().filter(h -> Boolean.TRUE.equals(h.getIsMakesUp())).count();

        Map<String, Object> result = new HashMap<>();
        result.put("studentId", studentId);
        result.put("courseId", courseId);
        result.put("startDate", startDate);
        result.put("endDate", endDate);
        result.put("totalCount", totalCount);
        result.put("presentCount", presentCount);
        result.put("absentCount", absentCount);
        result.put("lateCount", lateCount);
        result.put("leaveCount", leaveCount);
        result.put("makesUpCount", makesUpCount);
        result.put("attendanceRate", totalCount > 0 ? String.format("%.1f%%", (presentCount * 100.0 / totalCount)) : "0%");

        return Result.success(result);
    }

    private String getOperatorName(Integer operatorId) {
        if (operatorId == null) return "系统";
        return userRepository.findByUserId(operatorId)
                .map(User::getUsername)
                .orElse("未知用户");
    }
}
