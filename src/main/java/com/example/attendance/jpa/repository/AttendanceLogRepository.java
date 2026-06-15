package com.example.attendance.jpa.repository;

import com.example.attendance.jpa.entity.AttendanceLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AttendanceLogRepository extends JpaRepository<AttendanceLog, Long> {
    
    List<AttendanceLog> findByAttendanceId(Integer attendanceId);
    
    List<AttendanceLog> findByStudentId(Long studentId);
    
    List<AttendanceLog> findByCourseId(Integer courseId);
    
    List<AttendanceLog> findByOperatorId(Integer operatorId);
    
    List<AttendanceLog> findByActionType(String actionType);
    
    @Query("SELECT al FROM AttendanceLog al WHERE " +
           "(:studentId IS NULL OR al.studentId = :studentId) AND " +
           "(:courseId IS NULL OR al.courseId = :courseId) AND " +
           "(:operatorId IS NULL OR al.operatorId = :operatorId) AND " +
           "(:actionType IS NULL OR al.actionType = :actionType) AND " +
           "(:startTime IS NULL OR al.operationTime >= :startTime) AND " +
           "(:endTime IS NULL OR al.operationTime <= :endTime) " +
           "ORDER BY al.operationTime DESC")
    Page<AttendanceLog> findAttendanceLogWithConditions(
            @Param("studentId") Long studentId,
            @Param("courseId") Integer courseId,
            @Param("operatorId") Integer operatorId,
            @Param("actionType") String actionType,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            Pageable pageable
    );
    
    @Query("SELECT al FROM AttendanceLog al WHERE al.studentId = :studentId " +
           "ORDER BY al.operationTime DESC")
    Page<AttendanceLog> findLogsByStudentId(@Param("studentId") Long studentId, Pageable pageable);
}
