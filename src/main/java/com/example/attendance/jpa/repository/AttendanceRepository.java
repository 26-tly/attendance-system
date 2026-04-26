package com.example.attendance.jpa.repository;
import com.example.attendance.jpa.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;


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
    //1.分页查询所有
    Page<Attendance> findAll(Pageable pageable);
    //2.按课程id分页查询
    Page<Attendance> findByCourseId(Integer courseId,Pageable pageable);
    //3.按照用户id分页查询
    Page<Attendance> findByUserId(Integer userId,Pageable pageable);
    // 4. 按考勤状态分页查询（比如查所有迟到/旷课的记录）
    Page<Attendance> findByStatus(String status, Pageable pageable);

    // 5. 按日期范围分页查询（比如查某段时间内的考勤）
    Page<Attendance> findByAttendanceDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

    // 6. 多条件组合分页（课程ID + 日期 + 状态，最常用）
    Page<Attendance> findByCourseIdAndAttendanceDateBetweenAndStatus(
            Integer courseId,
            LocalDate startDate,
            LocalDate endDate,
            String status,
            Pageable pageable
    );

    // 7. 自定义JPQL多条件模糊/精准查询（支持动态条件拼接）
    @Query("SELECT a FROM Attendance a WHERE " +
            "(:courseId IS NULL OR a.courseId = :courseId) AND " +
            "(:userId IS NULL OR a.userId = :userId) AND " +
            "(:status IS NULL OR a.status = :status) AND " +
            "(:startDate IS NULL OR a.attendanceDate >= :startDate) AND " +
            "(:endDate IS NULL OR a.attendanceDate <= :endDate)")
    Page<Attendance> findAttendanceWithConditions(
            @Param("courseId") Integer courseId,
            @Param("userId") Integer userId,
            @Param("status") String status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );
}
