package com.example.attendance.jpa.controller;

import com.example.attendance.jpa.entity.Attendance;
import com.example.attendance.jpa.entity.Course;
import com.example.attendance.jpa.repository.CourseRepository;
import com.example.attendance.jpa.service.JpaAttendanceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
/**
 * 考勤模块控制层
 * 处理前端请求，调用JPA业务层，返回响应
 */
@RestController
@RequestMapping("/api/attendance")
public class JpaAttendanceController {
    private final JpaAttendanceService jpaAttendanceService;
    private final CourseRepository courseRepository;
    // ====================== 单一条件查询接口 ======================
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Attendance>> findByUserId(@PathVariable Integer userId) {
        List<Attendance> list = jpaAttendanceService.findByUserId(userId);
        return ResponseEntity.ok(list);
    }
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<Attendance>> findByCourseId(@PathVariable Integer courseId) {
        List<Attendance> list = jpaAttendanceService.findByCourseId(courseId);
        return ResponseEntity.ok(list);
    }
    @GetMapping("/date/{attendanceDate}")
    public ResponseEntity<List<Attendance>> findByAttendanceDate(@PathVariable LocalDate attendanceDate) {
        List<Attendance> list = jpaAttendanceService.findByAttendanceDate(attendanceDate);
        return ResponseEntity.ok(list);
    }
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Attendance>> findByStatus(@PathVariable String status) {
        List<Attendance> list = jpaAttendanceService.findByStatus(status);
        return ResponseEntity.ok(list);
    }
    // ====================== 两个条件组合查询接口 ======================
    @GetMapping("/user/{userId}/course/{courseId}")
    public ResponseEntity<List<Attendance>> findByUserIdAndCourseId(
            @PathVariable Integer userId,
            @PathVariable Integer courseId) {
        List<Attendance> list = jpaAttendanceService.findByUserIdAndCourseId(userId, courseId);
        return ResponseEntity.ok(list);
    }
    @GetMapping("/user/{userId}/date/{attendanceDate}")
    public ResponseEntity<List<Attendance>> findByUserIdAndAttendanceDate(
            @PathVariable Integer userId,
            @PathVariable LocalDate attendanceDate) {
        List<Attendance> list = jpaAttendanceService.findByUserIdAndAttendanceDate(userId, attendanceDate);
        return ResponseEntity.ok(list);
    }
    @GetMapping("/course/{courseId}/date/{attendanceDate}")
    public ResponseEntity<List<Attendance>> findByCourseIdAndAttendanceDate(
            @PathVariable Integer courseId,
            @PathVariable LocalDate attendanceDate) {
        List<Attendance> list = jpaAttendanceService.findByCourseIdAndAttendanceDate(courseId, attendanceDate);
        return ResponseEntity.ok(list);
    }
    @GetMapping("/course/{courseId}/status/{status}")
    public ResponseEntity<List<Attendance>> findByCourseIdAndStatus(
            @PathVariable Integer courseId,
            @PathVariable String status) {
        List<Attendance> list = jpaAttendanceService.findByCourseIdAndStatus(courseId, status);
        return ResponseEntity.ok(list);
    }
    // ====================== 三个条件组合查询接口 ======================
    @GetMapping("/user/{userId}/course/{courseId}/date/{attendanceDate}")
    public ResponseEntity<List<Attendance>> findByUserIdAndCourseIdAndAttendanceDate(
            @PathVariable Integer userId,
            @PathVariable Integer courseId,
            @PathVariable LocalDate attendanceDate) {
        List<Attendance> list = jpaAttendanceService.findByUserIdAndCourseIdAndAttendanceDate(userId, courseId, attendanceDate);
        return ResponseEntity.ok(list);
    }
    @GetMapping("/user/{userId}/course/{courseId}/status/{status}")
    public ResponseEntity<List<Attendance>> findByUserIdAndCourseIdAndStatus(
            @PathVariable Integer userId,
            @PathVariable Integer courseId,
            @PathVariable String status) {
        List<Attendance> list = jpaAttendanceService.findByUserIdAndCourseIdAndStatus(userId, courseId, status);
        return ResponseEntity.ok(list);
    }
    // ====================== 基础CRUD接口 ======================
    @PostMapping
    public ResponseEntity<Attendance> save(@RequestBody Attendance attendance) {
        Attendance saved = jpaAttendanceService.save(attendance);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Attendance> findById(@PathVariable Integer id) {
        Attendance attendance = jpaAttendanceService.findById(id);

        if (attendance == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(attendance);
    }
    @GetMapping
    public ResponseEntity<List<Attendance>> findAll() {
        List<Attendance> list = jpaAttendanceService.findAll();
        return ResponseEntity.ok(list);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Integer id) {
        jpaAttendanceService.deleteById(id);
        return ResponseEntity.noContent().build();
    }



    // 这里注入的是接口，不是实现类
    public JpaAttendanceController(JpaAttendanceService jpaAttendanceService, CourseRepository courseRepository) {
        this.jpaAttendanceService = jpaAttendanceService;
        this.courseRepository = courseRepository;
    }

    /**
     *
     * @param pageNum
     * @param pageSize
     * @param sortField 排序字段：attendanceDate,userId,courseId
     * @param sortDir 排序方向
     * @return
     * 基础分页+排序
     */
    @GetMapping("/list")
    public Page<Attendance> listAttendance(
            @RequestParam(defaultValue = "0") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "attendanceDate") String sortField,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        return jpaAttendanceService.listAttendance(pageNum, pageSize,sortField,sortDir);
    }
    @GetMapping("/list/course")
    public Page<Attendance> listByCourse(
            @RequestParam(required = false) Integer courseId,
            @RequestParam(defaultValue = "0") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "attendanceDate") String sortField,
            @RequestParam(defaultValue = "desc") String sortDir
    ){
        return jpaAttendanceService.listByCourseId( courseId,pageNum,pageSize,sortField,sortDir);
    }

    /**
     *
     * @param courseId
     * @param userId
     * @param status
     * @param startDate
     * @param endDate
     * @param pageNum
     * @param pageSize
     * @param sortField
     * @param sortDir
     * @return
     * 多条件筛选+分页+排序
     */
    @GetMapping("/list/conditions")
    public Page<Attendance> listAttendanceWithConditions(
            @RequestParam(required = false) Integer courseId,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(defaultValue = "0") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "attendanceDate") String sortField,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        return jpaAttendanceService.listAttendanceWithConditions(
                courseId, userId, status, startDate, endDate, pageNum, pageSize, sortField, sortDir
        );
    }

    /**
     * 打卡页面
     */
    @GetMapping("/attendance/checkin")
    public String checkInPage(Model model) {
        List<Course> courseList = courseRepository.findAll();
        model.addAttribute("courses", courseList);
        return "attendance/checkin"; // templates/attendance/checkin.html
    }

    /**
     * 考勤记录列表页面
     */
    @GetMapping("/attendance/list")
    public String attendanceList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        // 实际项目中 userId 从登录用户获取
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Attendance> records = jpaAttendanceService.getMyAttendance(1, pageable);

        model.addAttribute("records", records);
        return "attendance/list"; // templates/attendance/list.html
    }

    // ===================== 接口 =====================

    /**
     * 签到接口
     */
    @PostMapping("/attendance/doCheckIn")
    @ResponseBody
    public String doCheckIn(
            @RequestParam Integer userId,
            @RequestParam Integer courseId) {
        return jpaAttendanceService.checkIn(userId, courseId);
    }

    /**
     * 签退接口
     */
    @PostMapping("/attendance/doCheckOut")
    @ResponseBody
    public String doCheckOut(
            @RequestParam Integer userId,
            @RequestParam Integer courseId) {
        return jpaAttendanceService.checkOut(userId, courseId);
    }
}
