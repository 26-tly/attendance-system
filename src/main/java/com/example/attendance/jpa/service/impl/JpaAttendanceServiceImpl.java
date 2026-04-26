package com.example.attendance.jpa.service.impl;
import com.example.attendance.jpa.entity.Attendance;
import com.example.attendance.jpa.repository.AttendanceRepository;
import com.example.attendance.jpa.service.JpaAttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 考勤业务层实现类
 */
@Service
@RequiredArgsConstructor
public class JpaAttendanceServiceImpl implements JpaAttendanceService {
    // 注入Repository
    private final AttendanceRepository attendanceRepository;

    // ====================== 单一条件查询 ======================
    @Override
    public List<Attendance> findByUserId(Integer userId) {
        return attendanceRepository.findByUserId(userId);
    }

    @Override
    public List<Attendance> findByCourseId(Integer courseId) {
        return attendanceRepository.findByCourseId(courseId);
    }

    @Override
    public List<Attendance> findByAttendanceDate(LocalDate attendanceDate) {
        return attendanceRepository.findByAttendanceDate(attendanceDate);
    }

    @Override
    public List<Attendance> findByStatus(String status) {
        return attendanceRepository.findByStatus(status);
    }

    // ====================== 两个条件组合查询 ======================
    @Override
    public List<Attendance> findByUserIdAndCourseId(Integer userId, Integer courseId) {
        return attendanceRepository.findByUserIdAndCourseId(userId, courseId);
    }

    @Override
    public List<Attendance> findByUserIdAndAttendanceDate(Integer userId, LocalDate attendanceDate) {
        return attendanceRepository.findByUserIdAndAttendanceDate(userId, attendanceDate);
    }

    @Override
    public List<Attendance> findByCourseIdAndAttendanceDate(Integer courseId, LocalDate attendanceDate) {
        return attendanceRepository.findByCourseIdAndAttendanceDate(courseId, attendanceDate);
    }

    @Override
    public List<Attendance> findByCourseIdAndStatus(Integer courseId, String status) {
        return attendanceRepository.findByCourseIdAndStatus(courseId, status);
    }

    // ====================== 三个条件组合查询 ======================
    @Override
    public List<Attendance> findByUserIdAndCourseIdAndAttendanceDate(Integer userId, Integer courseId, LocalDate attendanceDate) {
        return attendanceRepository.findByUserIdAndCourseIdAndAttendanceDate(userId, courseId, attendanceDate);
    }

    @Override
    public List<Attendance> findByUserIdAndCourseIdAndStatus(Integer userId, Integer courseId, String status) {
        return attendanceRepository.findByUserIdAndCourseIdAndStatus(userId, courseId, status);
    }

    // ====================== 基础CRUD ======================
    @Override
    public Attendance save(Attendance attendance) {
        return attendanceRepository.save(attendance);
    }

    @Override
    public Attendance findById(Integer id) {
        Optional<Attendance> optional = attendanceRepository.findById(id);
        return optional.orElse(null);
    }

    @Override
    public List<Attendance> findAll() {
        return attendanceRepository.findAll();
    }

    @Override
    public void deleteById(Integer id) {
        attendanceRepository.deleteById(id);
    }
}
