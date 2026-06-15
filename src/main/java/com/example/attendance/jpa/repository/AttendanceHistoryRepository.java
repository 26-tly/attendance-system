package com.example.attendance.jpa.repository;

import com.example.attendance.jpa.entity.AttendanceHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceHistoryRepository extends JpaRepository<AttendanceHistory, Long> {
    
    List<AttendanceHistory> findByCourseId(Integer courseId);
    
    List<AttendanceHistory> findByStudentId(Long studentId);
    
    List<AttendanceHistory> findByUserId(Integer userId);
    
    List<AttendanceHistory> findByCourseIdAndAttendanceDate(Integer courseId, LocalDate attendanceDate);
    
    List<AttendanceHistory> findByStudentIdAndCourseId(Long studentId, Integer courseId);
    
    List<AttendanceHistory> findByAttendanceDateBetween(LocalDate startDate, LocalDate endDate);
    
    Optional<AttendanceHistory> findByUserIdAndCourseIdAndAttendanceDate(
            Integer userId, Integer courseId, LocalDate attendanceDate);
    
    @Query("SELECT ah FROM AttendanceHistory ah WHERE " +
           "(:courseId IS NULL OR ah.courseId = :courseId) AND " +
           "(:studentId IS NULL OR ah.studentId = :studentId) AND " +
           "(:userId IS NULL OR ah.userId = :userId) AND " +
           "(:status IS NULL OR ah.status = :status) AND " +
           "(:startDate IS NULL OR ah.attendanceDate >= :startDate) AND " +
           "(:endDate IS NULL OR ah.attendanceDate <= :endDate) " +
           "ORDER BY ah.attendanceDate DESC, ah.checkinTime DESC")
    Page<AttendanceHistory> findAttendanceHistoryWithConditions(
            @Param("courseId") Integer courseId,
            @Param("studentId") Long studentId,
            @Param("userId") Integer userId,
            @Param("status") String status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );
    
    @Query("SELECT COUNT(ah) FROM AttendanceHistory ah WHERE ah.courseId = :courseId AND ah.attendanceDate = :date")
    long countByCourseIdAndDate(@Param("courseId") Integer courseId, @Param("date") LocalDate date);
    
    @Query("SELECT COUNT(ah) FROM AttendanceHistory ah WHERE ah.courseId = :courseId AND ah.attendanceDate = :date AND ah.status = :status")
    long countByCourseIdAndDateAndStatus(@Param("courseId") Integer courseId, @Param("date") LocalDate date, @Param("status") String status);
}
