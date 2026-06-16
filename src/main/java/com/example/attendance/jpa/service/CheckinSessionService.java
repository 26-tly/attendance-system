package com.example.attendance.jpa.service;

import com.example.attendance.common.Result;
import com.example.attendance.jpa.entity.CheckinSession;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface CheckinSessionService {
    Result<Map<String, Object>> createSession(Integer teacherId, Integer courseId, 
                                              LocalDateTime startTime, LocalDateTime endTime);
    
    Result<CheckinSession> getSessionByCode(String sessionCode);
    
    Result<CheckinSession> getSessionById(Long sessionId);
    
    Result<Map<String, Object>> validateCheckin(String sessionCode, Integer userId, Integer studentId);
    
    Result<List<CheckinSession>> getTeacherSessions(Integer teacherId);
    
    Result<Map<String, Object>> getSessionStats(Long sessionId);
    
    Result<Void> closeSession(Long sessionId);
    
    Result<CheckinSession> getActiveSessionByCourse(Integer courseId);
    
    Result<List<Map<String, Object>>> getAllActiveSessions();
}