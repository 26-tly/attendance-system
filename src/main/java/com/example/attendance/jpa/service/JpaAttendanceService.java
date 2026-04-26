package com.example.attendance.jpa.service;
import com.example.attendance.jpa.entity.Attendance;
import org.springframework.data.domain.Page;
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


    /**
     * 基础全量分页查询
     * @param pageNum 页码（从0开始）
     * @param pageSize 每页条数
     * @return 分页结果
     */
    Page<Attendance> listAttendance(int pageNum, int pageSize,String sortField,String sortDir);

    /**
     * 按课程ID分页查询
     * @param courseId 课程ID
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    Page<Attendance> listByCourseId(Integer courseId, int pageNum, int pageSize,String sortField,String sortDir);

    /**
     * 多条件动态分页查询（核心方法）
     * @param courseId 课程ID（可选）
     * @param userId 用户ID（可选）
     * @param status 考勤状态（可选）
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    Page<Attendance> listAttendanceWithConditions(
            Integer courseId,
            Integer userId,
            String status,
            LocalDate startDate,
            LocalDate endDate,
            int pageNum,
            int pageSize,
            String sortField,
            String sortDir
    );
}
