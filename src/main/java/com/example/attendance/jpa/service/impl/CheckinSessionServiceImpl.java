package com.example.attendance.jpa.service.impl;

import com.example.attendance.common.Result;
import com.example.attendance.jpa.entity.Attendance;
import com.example.attendance.jpa.entity.CheckinSession;
import com.example.attendance.jpa.entity.Course;
import com.example.attendance.jpa.entity.Student;
import com.example.attendance.jpa.repository.AttendanceRepository;
import com.example.attendance.jpa.repository.CheckinSessionRepository;
import com.example.attendance.jpa.repository.CourseRepository;
import com.example.attendance.jpa.repository.StudentRepository;
import com.example.attendance.jpa.service.CheckinSessionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    public CheckinSessionServiceImpl(CheckinSessionRepository sessionRepository,
                                    CourseRepository courseRepository,
                                    StudentRepository studentRepository,
                                    AttendanceRepository attendanceRepository) {
        this.sessionRepository = sessionRepository;
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
        this.attendanceRepository = attendanceRepository;
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

        // 确定使用的学生ID
        Integer checkUserId = null;
        if (studentId != null && studentId > 0) {
            // 优先使用studentId
            Optional<Student> studentOpt = studentRepository.findById(Long.valueOf(studentId));
            if (studentOpt.isEmpty()) {
                return Result.error("学生不存在");
            }
            checkUserId = studentId;
        } else if (userId != null && userId > 0) {
            // 回退到userId
            Optional<Student> studentOpt = studentRepository.findById(Long.valueOf(userId));
            if (studentOpt.isEmpty()) {
                return Result.error("学生不存在");
            }
            checkUserId = userId;
        } else {
            return Result.error("用户ID无效");
        }

        Optional<Attendance> existingAttendance = attendanceRepository.findByUserIdAndCourseIdAndCheckinTimeBetween(
                checkUserId, session.getCourseId(), session.getStartTime(), session.getEndTime());
        
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
        int total = session.getTotalStudents() != null ? session.getTotalStudents() : 0;
        int checked = session.getCheckedStudents() != null ? session.getCheckedStudents() : 0;
        int unchecked = total - checked;
        double rate = total > 0 ? (checked * 100.0 / total) : 0;

        result.put("sessionId", session.getSessionId());
        result.put("courseId", session.getCourseId());
        result.put("totalStudents", total);
        result.put("checkedStudents", checked);
        result.put("uncheckedStudents", unchecked);
        result.put("checkinRate", String.format("%.1f", rate));
        result.put("status", session.getStatus());
        result.put("startTime", session.getStartTime());
        result.put("endTime", session.getEndTime());

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
    public Result<List<CheckinSession>> getAllActiveSessions() {
        List<CheckinSession> sessions = sessionRepository.findActiveSessions(LocalDateTime.now());
        return Result.success(sessions);
    }
}