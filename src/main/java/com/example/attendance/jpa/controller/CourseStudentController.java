package com.example.attendance.jpa.controller;

import com.example.attendance.common.Result;
import com.example.attendance.jpa.service.CourseStudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/course-student")
public class CourseStudentController {

    private final CourseStudentService courseStudentService;

    public CourseStudentController(CourseStudentService courseStudentService) {
        this.courseStudentService = courseStudentService;
    }

    @GetMapping("/course/{courseId}/students")
    public ResponseEntity<Result<List<Map<String, Object>>>> getStudentsByCourseId(@PathVariable Integer courseId) {
        return ResponseEntity.ok(courseStudentService.getStudentsByCourseId(courseId));
    }

    @GetMapping("/student/{studentId}/courses")
    public ResponseEntity<Result<List<Map<String, Object>>>> getCoursesByStudentId(@PathVariable Long studentId) {
        return ResponseEntity.ok(courseStudentService.getCoursesByStudentId(studentId));
    }

    @PostMapping("/add")
    public ResponseEntity<Result<Void>> addStudentToCourse(@RequestBody Map<String, Object> request) {
        Integer courseId = getInteger(request, "courseId");
        Long studentId = getLong(request, "studentId");
        return ResponseEntity.ok(courseStudentService.addStudentToCourse(courseId, studentId));
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Result<Void>> removeStudentFromCourse(
            @RequestParam Integer courseId,
            @RequestParam Long studentId) {
        return ResponseEntity.ok(courseStudentService.removeStudentFromCourse(courseId, studentId));
    }

    @PostMapping("/batch-add")
    public ResponseEntity<Result<Map<String, Object>>> batchAddStudentsToCourse(@RequestBody Map<String, Object> request) {
        Integer courseId = getInteger(request, "courseId");
        List<?> studentIdsRaw = (List<?>) request.get("studentIds");
        List<Long> studentIds = studentIdsRaw.stream()
                .map(id -> {
                    if (id instanceof Long) {
                        return (Long) id;
                    } else if (id instanceof Integer) {
                        return ((Integer) id).longValue();
                    } else if (id instanceof String) {
                        return Long.parseLong((String) id);
                    }
                    return null;
                })
                .filter(id -> id != null)
                .toList();
        Result<Map<String, Object>> result = courseStudentService.batchAddStudentsToCourse(courseId, studentIds);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/course/{courseId}/info")
    public ResponseEntity<Result<Map<String, Object>>> getCourseStudentInfo(@PathVariable Integer courseId) {
        return ResponseEntity.ok(courseStudentService.getCourseStudentInfo(courseId));
    }

    private Integer getInteger(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof String) {
            return Integer.parseInt((String) value);
        }
        return null;
    }

    private Long getLong(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Long) {
            return (Long) value;
        } else if (value instanceof Integer) {
            return ((Integer) value).longValue();
        } else if (value instanceof String) {
            return Long.parseLong((String) value);
        }
        return null;
    }
}
