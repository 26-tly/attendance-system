package com.example.attendance.jpa.repository;

import com.example.attendance.jpa.entity.LeaveApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveApplicationRepository extends JpaRepository<LeaveApplication, Integer> {
    List<LeaveApplication> findByUserId(Integer userId);

    List<LeaveApplication> findByStatus(String status);

    List<LeaveApplication> findByCourseId(Integer courseId);

    Page<LeaveApplication> findByUserId(Integer userId, Pageable pageable);

    Page<LeaveApplication> findByStatus(String status, Pageable pageable);

    Page<LeaveApplication> findByCourseIdAndStatus(Integer courseId, String status, Pageable pageable);

    List<LeaveApplication> findByUserIdAndCourseIdAndStartDateBetween(Integer userId, Integer courseId, LocalDate startDate, LocalDate endDate);

    List<LeaveApplication> findByStatusOrderByApplyTimeDesc(String status);
}