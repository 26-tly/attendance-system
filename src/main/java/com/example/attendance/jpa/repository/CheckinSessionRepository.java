package com.example.attendance.jpa.repository;

import com.example.attendance.jpa.entity.CheckinSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CheckinSessionRepository extends JpaRepository<CheckinSession, Long> {
    Optional<CheckinSession> findBySessionCode(String sessionCode);
    
    List<CheckinSession> findByTeacherIdOrderByCreatedTimeDesc(Integer teacherId);
    
    List<CheckinSession> findByCourseIdAndStatusOrderByCreatedTimeDesc(Integer courseId, String status);
    
    @Query("SELECT s FROM CheckinSession s WHERE s.status = 'active' AND s.endTime > :now")
    List<CheckinSession> findActiveSessions(@Param("now") LocalDateTime now);
    
    @Modifying
    @Query("UPDATE CheckinSession s SET s.status = 'expired' WHERE s.status = 'active' AND s.endTime < :now")
    int expireSessions(@Param("now") LocalDateTime now);
    
    @Modifying
    @Query("UPDATE CheckinSession s SET s.checkedStudents = s.checkedStudents + 1 WHERE s.sessionId = :sessionId")
    int incrementCheckedStudents(@Param("sessionId") Long sessionId);
    
    Optional<CheckinSession> findByCourseIdAndStatus(Integer courseId, String status);
}