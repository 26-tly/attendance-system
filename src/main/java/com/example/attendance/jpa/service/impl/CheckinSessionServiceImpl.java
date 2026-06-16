package com.example.attendance.jpa.service.impl;

import com.example.attendance.common.Result;
import com.example.attendance.jpa.entity.Attendance;
import com.example.attendance.jpa.entity.AttendanceHistory;
import com.example.attendance.jpa.entity.CheckinSession;
import com.example.attendance.jpa.entity.Course;
import com.example.attendance.jpa.entity.Student;
import com.example.attendance.jpa.entity.User;
import com.example.attendance.jpa.repository.AttendanceHistoryRepository;
import com.example.attendance.jpa.repository.AttendanceRepository;
import com.example.attendance.jpa.repository.CheckinSessionRepository;
import com.example.attendance.jpa.repository.CourseRepository;
import com.example.attendance.jpa.repository.StudentRepository;
import com.example.attendance.jpa.repository.UserRepository;
import com.example.attendance.jpa.service.CheckinSessionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class CheckinSessionServiceImpl implements CheckinSessionService {

    private final CheckinSessionRepository sessionRepository;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final AttendanceRepository attendanceRepository;
    private final AttendanceHistoryRepository attendanceHistoryRepository;
    private final UserRepository userRepository;

    public CheckinSessionServiceImpl(CheckinSessionRepository sessionRepository,
                                    CourseRepository courseRepository,
                                    StudentRepository studentRepository,
                                    AttendanceRepository attendanceRepository,
                                    AttendanceHistoryRepository attendanceHistoryRepository,
                                    UserRepository userRepository) {
        this.sessionRepository = sessionRepository;
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
        this.attendanceRepository = attendanceRepository;
        this.attendanceHistoryRepository = attendanceHistoryRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Map<String, Object>> createSession(Integer teacherId, Integer courseId,
                                                     LocalDateTime startTime, LocalDateTime endTime) {
        Map<String, Object> result = new HashMap<>();
        
        if (teacherId == null || teacherId <= 0) {
            return Result.error("教师ID无效");
        }
        if (courseId == null || courseId <= 0) {
            return Result.error("课程ID无效");
        }
        if (startTime == null || endTime == null) {
            return Result.error("请设置签到时间");
        }
        if (startTime.isAfter(endTime)) {
            return Result.error("开始时间不能晚于结束时间");
        }

        Optional<Course> courseOpt = courseRepository.findById(courseId);
        if (courseOpt.isEmpty()) {
            return Result.error("课程不存在");
        }

        sessionRepository.expireSessions(LocalDateTime.now());
        
        List<CheckinSession> activeSessions = sessionRepository.findByCourseIdAndStatusOrderByCreatedTimeDesc(courseId, "active");
        if (!activeSessions.isEmpty()) {
            return Result.error("该课程已有进行中的签到会话，请先结束当前会话");
        }

        String sessionCode = generateSessionCode();
        
        CheckinSession session = new CheckinSession();
        session.setTeacherId(teacherId);
        session.setCourseId(courseId);
        session.setSessionCode(sessionCode);
        session.setStartTime(startTime);
        session.setEndTime(endTime);
        session.setStatus("active");

        long studentCount = studentRepository.count();
        session.setTotalStudents((int) studentCount);
        session.setCheckedStudents(0);

        CheckinSession saved = sessionRepository.save(session);

        result.put("sessionId", saved.getSessionId());
        result.put("sessionCode", saved.getSessionCode());
        result.put("startTime", saved.getStartTime());
        result.put("endTime", saved.getEndTime());
        result.put("courseName", courseOpt.get().getCourseName());
        result.put("totalStudents", saved.getTotalStudents());

        return Result.success(result);
    }

    @Override
    public Result<CheckinSession> getSessionByCode(String sessionCode) {
        if (sessionCode == null || sessionCode.isEmpty()) {
            return Result.error("会话码不能为空");
        }

        Optional<CheckinSession> sessionOpt = sessionRepository.findBySessionCode(sessionCode);
        if (sessionOpt.isEmpty()) {
            return Result.error("会话不存在");
        }

        CheckinSession session = sessionOpt.get();
        LocalDateTime now = LocalDateTime.now();

        if ("expired".equals(session.getStatus())) {
            return Result.error("会话已过期");
        }

        return Result.success(session);
    }

    @Override
    public Result<CheckinSession> getSessionById(Long sessionId) {
        if (sessionId == null || sessionId <= 0) {
            return Result.error("会话ID无效");
        }

        Optional<CheckinSession> sessionOpt = sessionRepository.findById(sessionId);
        if (sessionOpt.isEmpty()) {
            return Result.error("会话不存在");
        }

        return Result.success(sessionOpt.get());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Map<String, Object>> validateCheckin(String sessionCode, Integer userId, Integer studentId) {
        Map<String, Object> result = new HashMap<>();

        if (sessionCode == null || sessionCode.isEmpty()) {
            return Result.error("会话码不能为空");
        }

        Optional<CheckinSession> sessionOpt = sessionRepository.findBySessionCode(sessionCode);
        if (sessionOpt.isEmpty()) {
            return Result.error("会话不存在");
        }

        CheckinSession session = sessionOpt.get();
        LocalDateTime now = LocalDateTime.now();

        if (!"active".equals(session.getStatus())) {
            return Result.error("会话已结束");
        }

        if (now.isBefore(session.getStartTime())) {
            return Result.error("签到尚未开始");
        }

        if (now.isAfter(session.getEndTime())) {
            return Result.error("签到已结束");
        }

        // 确定使用的学生ID和用户ID
        Integer checkUserId = null;
        Integer checkStudentId = null;
        Integer inputId = (studentId != null && studentId > 0) ? studentId : userId;
        
        if (inputId != null && inputId > 0) {
            // 1. 先尝试作为 Student 表的主键查找
            Optional<Student> studentOpt = studentRepository.findById(Long.valueOf(inputId));
            if (studentOpt.isPresent()) {
                checkStudentId = inputId;
                // 通过学号查找用户
                User user = userRepository.findByUsername(studentOpt.get().getStudentNo());
                if (user != null) {
                    checkUserId = user.getUserId();
                }
            } else {
                // 2. 作为 User 表的主键查找
                User user = userRepository.findById(inputId).orElse(null);
                if (user != null) {
                    checkUserId = inputId;
                    // 3. 通过学号(username)查找 Student
                    List<Student> students = studentRepository.findByStudentNo(user.getUsername());
                    if (!students.isEmpty()) {
                        checkStudentId = students.get(0).getId().intValue();
                    } else {
                        System.out.println("[validateCheckin] User表有该用户(username=" + user.getUsername() + ")，但Student表无对应学生记录");
                    }
                } else {
                    System.out.println("[validateCheckin] Student表和User表都找不到ID=" + inputId + "的记录");
                }
            }
        }
        
        if (checkUserId == null) {
            return Result.error("学生不存在");
        }

        Optional<Attendance> existingAttendance = attendanceRepository.findByUserIdAndSessionCode(
                checkUserId, sessionCode);
        
        if (existingAttendance.isPresent()) {
            return Result.error("您已完成签到");
        }

        Attendance attendance = new Attendance();
        attendance.setUserId(checkUserId);
        attendance.setCourseId(session.getCourseId());
        attendance.setCheckinTime(LocalDateTime.now());
        attendance.setAttendanceDate(LocalDateTime.now().toLocalDate());
        attendance.setStatus("present");
        attendance.setSessionCode(sessionCode);

        attendanceRepository.save(attendance);

        // 同步保存到 attendance_history 表（用于教师端历史记录查询）
        try {
            AttendanceHistory history = new AttendanceHistory();
            history.setCourseId(session.getCourseId());
            history.setStudentId(checkStudentId != null ? checkStudentId.longValue() : (checkUserId != null ? checkUserId.longValue() : 0L));
            history.setUserId(checkUserId);
            history.setAttendanceDate(LocalDateTime.now().toLocalDate());
            history.setSessionCode(sessionCode);
            history.setStatus("present");
            history.setCheckinTime(LocalDateTime.now());
            history.setIsMakesUp(false);
            attendanceHistoryRepository.save(history);
            System.out.println("[validateCheckin] 同步保存到 attendance_history 成功: userId=" + checkUserId + ", studentId=" + checkStudentId);
        } catch (Exception e) {
            System.err.println("[validateCheckin] 保存 attendance_history 失败: " + e.getMessage());
        }

        sessionRepository.incrementCheckedStudents(session.getSessionId());

        result.put("success", true);
        result.put("message", "签到成功");
        result.put("sessionId", session.getSessionId());
        result.put("courseId", session.getCourseId());

        return Result.success(result);
    }

    @Override
    public Result<List<CheckinSession>> getTeacherSessions(Integer teacherId) {
        if (teacherId == null || teacherId <= 0) {
            return Result.error("教师ID无效");
        }

        List<CheckinSession> sessions = sessionRepository.findByTeacherIdOrderByCreatedTimeDesc(teacherId);
        return Result.success(sessions);
    }

    @Override
    public Result<Map<String, Object>> getSessionStats(Long sessionId) {
        Map<String, Object> result = new HashMap<>();

        Optional<CheckinSession> sessionOpt = sessionRepository.findById(sessionId);
        if (sessionOpt.isEmpty()) {
            return Result.error("会话不存在");
        }

        CheckinSession session = sessionOpt.get();
        
        List<Attendance> attendanceList = attendanceRepository.findBySessionCode(session.getSessionCode());
        int actualCheckedCount = attendanceList.size();
        
        int total = session.getTotalStudents() != null ? session.getTotalStudents() : 0;
        int unchecked = total - actualCheckedCount;
        double rate = total > 0 ? (actualCheckedCount * 100.0 / total) : 0;

        result.put("sessionId", session.getSessionId());
        result.put("sessionCode", session.getSessionCode());
        result.put("courseId", session.getCourseId());
        result.put("totalStudents", total);
        result.put("checkedStudents", actualCheckedCount);
        result.put("uncheckedStudents", unchecked);
        result.put("checkinRate", String.format("%.1f", rate));
        result.put("status", session.getStatus());
        result.put("startTime", session.getStartTime());
        result.put("endTime", session.getEndTime());
        
        result.put("checkedCount", actualCheckedCount);
        result.put("totalCount", total);

        // 查询已签到的学生列表
        List<Map<String, Object>> checkedInStudents = new ArrayList<>();
        for (Attendance att : attendanceList) {
            Map<String, Object> studentInfo = new HashMap<>();
            studentInfo.put("userId", att.getUserId());
            studentInfo.put("courseId", att.getCourseId());
            studentInfo.put("checkinTime", att.getCheckinTime());
            studentInfo.put("status", att.getStatus());
            
            // 查询学生信息
            Optional<User> userOpt = userRepository.findById(att.getUserId());
            if (userOpt.isPresent()) {
                studentInfo.put("username", userOpt.get().getUsername());
                studentInfo.put("realName", userOpt.get().getUsername());

                // 通过学号查询学生详细信息
                List<Student> students = studentRepository.findByStudentNo(userOpt.get().getUsername());
                if (!students.isEmpty()) {
                    Student student = students.get(0);
                    studentInfo.put("studentNo", student.getStudentNo());
                    studentInfo.put("studentName", student.getName());
                }
            }
            checkedInStudents.add(studentInfo);
        }
        result.put("checkedInStudents", checkedInStudents);
        result.put("actualCheckedCount", checkedInStudents.size()); // 实时已签到人数

        return Result.success(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> closeSession(Long sessionId) {
        Optional<CheckinSession> sessionOpt = sessionRepository.findById(sessionId);
        if (sessionOpt.isEmpty()) {
            return Result.error("会话不存在");
        }

        CheckinSession session = sessionOpt.get();
        session.setStatus("closed");
        sessionRepository.save(session);

        return Result.success();
    }

    @Override
    public Result<CheckinSession> getActiveSessionByCourse(Integer courseId) {
        if (courseId == null || courseId <= 0) {
            return Result.error("课程ID无效");
        }

        Optional<CheckinSession> sessionOpt = sessionRepository.findByCourseIdAndStatus(courseId, "active");
        if (sessionOpt.isEmpty()) {
            return Result.error("当前没有进行中的签到会话");
        }

        return Result.success(sessionOpt.get());
    }

    private String generateSessionCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    @Override
    public Result<List<Map<String, Object>>> getAllActiveSessions() {
        List<CheckinSession> sessions = sessionRepository.findActiveSessions(LocalDateTime.now());
        
        List<Map<String, Object>> resultList = sessions.stream().map(session -> {
            Map<String, Object> map = new HashMap<>();
            map.put("sessionId", session.getSessionId());
            map.put("courseId", session.getCourseId());
            map.put("teacherId", session.getTeacherId());
            map.put("sessionCode", session.getSessionCode());
            map.put("status", session.getStatus());
            map.put("startTime", session.getStartTime());
            map.put("endTime", session.getEndTime());
            map.put("createdTime", session.getCreatedTime());
            
            int total = session.getTotalStudents() != null ? session.getTotalStudents() : 0;
            int checked = (int) attendanceRepository.countBySessionCode(session.getSessionCode());
            map.put("totalCount", total);
            map.put("checkedCount", checked);
            
            Optional<Course> courseOpt = courseRepository.findById(session.getCourseId());
            if (courseOpt.isPresent()) {
                map.put("courseName", courseOpt.get().getCourseName());
            } else {
                map.put("courseName", "未知课程");
            }
            
            return map;
        }).toList();
        
        return Result.success(resultList);
    }
}