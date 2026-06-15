package com.example.attendance.jpa.controller;

import com.example.attendance.common.Result;
import com.example.attendance.jpa.entity.Course;
import com.example.attendance.jpa.service.JpaCourseService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/course")
public class JpaCourseController {

    private final JpaCourseService courseService;

    public JpaCourseController(JpaCourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping
    public ResponseEntity<Result<Course>> addCourse(@RequestBody Course course) {
        Course savedCourse = courseService.saveCourse(course);
        return ResponseEntity.ok(Result.success(savedCourse));
    }

    @PutMapping("/{courseId}")
    public ResponseEntity<Result<Course>> updateCourse(@PathVariable Integer courseId, @RequestBody Course course) {
        course.setCourseId(courseId);
        Course savedCourse = courseService.saveCourse(course);
        return ResponseEntity.ok(Result.success(savedCourse));
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<Result<Void>> deleteCourse(@PathVariable Integer courseId) {
        return ResponseEntity.ok(courseService.deleteCourse(courseId));
    }

    @DeleteMapping("/batch")
    public ResponseEntity<Result<Integer>> batchDeleteCourses(@RequestBody List<Integer> courseIds) {
        return ResponseEntity.ok(courseService.batchDeleteCourses(courseIds));
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<Course> getById(@PathVariable Integer courseId) {
        Course course = courseService.getById(courseId);
        if (course == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(course);
    }

    @GetMapping("/list")
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @GetMapping
    public ResponseEntity<List<Course>> getAllCoursesSimple() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @GetMapping("/page")
    public ResponseEntity<Page<Course>> getCourses(
            @RequestParam(defaultValue = "0") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return ResponseEntity.ok(courseService.getCourses(pageNum, pageSize));
    }
}