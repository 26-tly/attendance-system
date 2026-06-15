package com.example.attendance.jpa.controller;

import com.example.attendance.common.Result;
import com.example.attendance.jpa.entity.CheckinSession;
import com.example.attendance.jpa.service.CheckinSessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/checkin")
public class CheckinSessionController {

    private final CheckinSessionService sessionService;

    public CheckinSessionController(CheckinSessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping("/session/create")
    public ResponseEntity<Result<Map<String, Object>>> createSession(@RequestBody Map<String, Object> request) {
        try {
            Integer teacherId = getInteger(request, "teacherId");
            Integer courseId = getInteger(request, "courseId");
            String startTimeStr = (String) request.get("startTime");
            String endTimeStr = (String) request.get("endTime");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            LocalDateTime startTime = LocalDateTime.parse(startTimeStr, formatter);
            LocalDateTime endTime = LocalDateTime.parse(endTimeStr, formatter);

            return ResponseEntity.ok(sessionService.createSession(teacherId, courseId, startTime, endTime));
        } catch (Exception e) {
            return ResponseEntity.ok(Result.error("参数解析错误: " + e.getMessage()));
        }
    }

    @GetMapping("/session/{sessionCode}")
    public ResponseEntity<Result<CheckinSession>> getSessionByCode(@PathVariable String sessionCode) {
        return ResponseEntity.ok(sessionService.getSessionByCode(sessionCode));
    }

    @PostMapping("/validate")
    public ResponseEntity<Result<Map<String, Object>>> validateCheckin(@RequestBody Map<String, Object> request) {
        String sessionCode = (String) request.get("sessionCode");
        Integer userId = getInteger(request, "userId");
        Integer studentId = getInteger(request, "studentId");
        return ResponseEntity.ok(sessionService.validateCheckin(sessionCode, userId, studentId));
    }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<Result<List<CheckinSession>>> getTeacherSessions(@PathVariable Integer teacherId) {
        return ResponseEntity.ok(sessionService.getTeacherSessions(teacherId));
    }

    @GetMapping("/stats/{sessionId}")
    public ResponseEntity<Result<Map<String, Object>>> getSessionStats(@PathVariable Long sessionId) {
        return ResponseEntity.ok(sessionService.getSessionStats(sessionId));
    }

    @PostMapping("/session/{sessionId}/close")
    public ResponseEntity<Result<Void>> closeSession(@PathVariable Long sessionId) {
        return ResponseEntity.ok(sessionService.closeSession(sessionId));
    }

    @GetMapping("/course/{courseId}/active")
    public ResponseEntity<Result<CheckinSession>> getActiveSessionByCourse(@PathVariable Integer courseId) {
        return ResponseEntity.ok(sessionService.getActiveSessionByCourse(courseId));
    }

    @GetMapping("/active")
    public ResponseEntity<Result<List<CheckinSession>>> getAllActiveSessions() {
        return ResponseEntity.ok(sessionService.getAllActiveSessions());
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
}