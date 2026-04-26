package com.example.attendance.jpa.controller;
import com.example.attendance.jpa.entity.Attendance;
import com.example.attendance.jpa.service.JpaAttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
/**
 * 考勤模块控制层
 * 处理前端请求，调用JPA业务层，返回响应
 */
@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class JpaAttendanceController {
    private final JpaAttendanceService jpaAttendanceService;
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
}
