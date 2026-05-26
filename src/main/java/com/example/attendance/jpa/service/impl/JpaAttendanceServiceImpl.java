package com.example.attendance.jpa.service.impl;

import com.example.attendance.jpa.entity.Attendance;
import com.example.attendance.jpa.entity.Course;
import com.example.attendance.jpa.repository.AttendanceRepository;
import com.example.attendance.jpa.repository.CourseRepository;
import com.example.attendance.jpa.service.JpaAttendanceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 考勤业务层实现类
 */
@Service
@Transactional(readOnly = true) // 只读事务，提升查询性能

public class JpaAttendanceServiceImpl implements JpaAttendanceService {
    // 注入Repository
    private final AttendanceRepository attendanceRepository;
    private final CourseRepository  courseRepository;
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



    // 构造器注入（Spring推荐方式）
    public JpaAttendanceServiceImpl(AttendanceRepository attendanceRepository, CourseRepository courseRepository) {
        this.attendanceRepository = attendanceRepository;
        this.courseRepository = courseRepository;
    }

    // 封装排序
    private Sort getSort(String sortField, String sortDir){
        // 默认排序字段 & 方向
        String field = (sortField == null || sortField.isBlank()) ? "attendanceDate" : sortField;
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDir)
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        return Sort.by(direction, field);
    }

    @Override
    public Page<Attendance> listAttendance(int pageNum, int pageSize,String sortField,String sortDir) {
        Sort sort = getSort(sortField,sortDir);
        // 按考勤日期倒序排序
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by(Sort.Direction.DESC, "attendanceDate"));
        return attendanceRepository.findAll(pageable);
    }

    @Override
    public Page<Attendance> listByCourseId(Integer courseId, int pageNum, int pageSize,String sortField,String sortDir) {
        Sort sort = getSort(sortField,sortDir);
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by(Sort.Direction.DESC, "attendanceDate"));
        return attendanceRepository.findByCourseId(courseId, pageable);
    }

    @Override
    public Page<Attendance> listAttendanceWithConditions(
            Integer courseId,
            Integer userId,
            String status,
            LocalDate startDate,
            LocalDate endDate,
            int pageNum,
            int pageSize,
            String sortField,
            String sortDir
    ) {
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by(Sort.Direction.DESC, "attendanceDate"));
        // 调用Repository的多条件查询方法
        return attendanceRepository.findAttendanceWithConditions(
                courseId, userId, status, startDate, endDate, pageable
        );
    }


    /**
     * 签到
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String checkIn(Integer userId, Integer courseId) {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        // 1. 查询课程
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("课程不存在"));

        // 2. 查询今天是否已签到 → 这里改成 List
        List<Attendance> existList = attendanceRepository
                .findByUserIdAndCourseIdAndAttendanceDate(userId, courseId, today);

        if (!existList.isEmpty()) {
            return "今日已签到，请勿重复操作";
        }

        // 3. 判断是否迟到
        String status;
        if (now.isAfter(course.getStartTime())) {
            status = "late";
        } else {
            status = "present";
        }

        // 4. 构建考勤记录
        Attendance attendance = Attendance.builder()
                .userId(userId)
                .courseId(courseId)
                .attendanceDate(today)
                .status(status)
                .checkinTime(now)
                .build();

        attendanceRepository.save(attendance);
        return "签到成功，状态：" + status;
    }

    /**
     * 签退
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String checkOut(Integer userId, Integer courseId) {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        // 1. 查询今日签到记录 → 改成 List
        List<Attendance> attendanceList = attendanceRepository
                .findByUserIdAndCourseIdAndAttendanceDate(userId, courseId, today);

        if (attendanceList.isEmpty()) {
            return "未找到签到记录，无法签退";
        }

        // 获取第一条
        Attendance attendance = attendanceList.get(0);

        // 2. 查询课程
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("课程不存在"));

        // 3. 判断早退
        if (now.isBefore(course.getEndTime())) {
            attendance.setStatus("early");
        }

        // 4. 保存签退时间
        attendance.setCheckoutTime(now);
        attendanceRepository.save(attendance);

        return "签退成功";
    }

    /**
     * 查询个人考勤
     */
    @Override
    public Page<Attendance> getMyAttendance(Integer userId, Pageable pageable) {
        return attendanceRepository.findByUserId(userId, pageable);
    }
}
