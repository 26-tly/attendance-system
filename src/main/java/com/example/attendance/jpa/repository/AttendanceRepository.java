package com.example.attendance.jpa.repository;
import com.example.attendance.jpa.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * 考勤表数据访问层 - 查询条件完全拆分版
 * 继承 JpaRepository 自带基础 CRUD
 */
@Repository
public interface AttendanceRepository extends JpaRepository<Attendance,Integer>{
    // ====================== 单一条件查询 ======================
    /**
     * 按用户ID查询所有考勤记录
     */
    List<Attendance> findByUserId(Integer userId);

    /**
     * 按课程ID查询所有考勤记录
     */
    List<Attendance> findByCourseId(Integer courseId);

    /**
     * 按考勤日期查询所有考勤记录
     */
    List<Attendance> findByAttendanceDate(LocalDate attendanceDate);

    /**
     * 按考勤状态查询（present/absent/leave）
     */
    List<Attendance> findByStatus(String status);


    // ====================== 两个条件组合查询 ======================
    /**
     * 用户ID + 课程ID
     */
    List<Attendance> findByUserIdAndCourseId(Integer userId, Integer courseId);

    /**
     * 用户ID + 考勤日期
     */
    List<Attendance> findByUserIdAndAttendanceDate(Integer userId, LocalDate attendanceDate);

    /**
     * 课程ID + 考勤日期
     */
    List<Attendance> findByCourseIdAndAttendanceDate(Integer courseId, LocalDate attendanceDate);

    /**
     * 课程ID + 考勤状态
     */
    List<Attendance> findByCourseIdAndStatus(Integer courseId, String status);


    // ====================== 三个条件组合查询 ======================
    /**
     * 用户ID + 课程ID + 考勤日期（精确唯一记录查询）
     */
    List<Attendance> findByUserIdAndCourseIdAndAttendanceDate(
            Integer userId,
            Integer courseId,
            LocalDate attendanceDate
    );

    /**
     * 用户ID + 课程ID + 考勤状态
     */
    List<Attendance> findByUserIdAndCourseIdAndStatus(
            Integer userId,
            Integer courseId,
            String status
    );
}
