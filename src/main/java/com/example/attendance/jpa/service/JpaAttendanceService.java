package com.example.attendance.jpa.service;
import com.example.attendance.jpa.entity.Attendance;
import java.time.LocalDate;
import java.util.List;

/**
 * 考勤业务层接口
 */
public interface JpaAttendanceService {

    // ====================== 单一条件查询 ======================
    List<Attendance> findByUserId(Integer userId);

    List<Attendance> findByCourseId(Integer courseId);

    List<Attendance> findByAttendanceDate(LocalDate attendanceDate);

    List<Attendance> findByStatus(String status);

    // ====================== 两个条件组合查询 ======================
    List<Attendance> findByUserIdAndCourseId(Integer userId, Integer courseId);

    List<Attendance> findByUserIdAndAttendanceDate(Integer userId, LocalDate attendanceDate);

    List<Attendance> findByCourseIdAndAttendanceDate(Integer courseId, LocalDate attendanceDate);

    List<Attendance> findByCourseIdAndStatus(Integer courseId, String status);

    // ====================== 三个条件组合查询 ======================
    List<Attendance> findByUserIdAndCourseIdAndAttendanceDate(Integer userId, Integer courseId, LocalDate attendanceDate);

    List<Attendance> findByUserIdAndCourseIdAndStatus(Integer userId, Integer courseId, String status);

    // ====================== 基础CRUD ======================
    Attendance save(Attendance attendance);

    Attendance findById(Integer id);

    List<Attendance> findAll();

    void deleteById(Integer id);
}
