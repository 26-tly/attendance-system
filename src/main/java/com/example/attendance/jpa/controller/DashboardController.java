package com.example.attendance.jpa.controller;

import com.example.attendance.common.Result;
import com.example.attendance.jpa.repository.AttendanceRepository;
import com.example.attendance.jpa.repository.CourseRepository;
import com.example.attendance.jpa.repository.LeaveApplicationRepository;
import com.example.attendance.jpa.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private LeaveApplicationRepository leaveApplicationRepository;

    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        long courseCount = courseRepository.count();
        long studentCount = studentRepository.count();
        long pendingLeave = leaveApplicationRepository.findByStatus("pending").size();
        
        long totalAttendance = attendanceRepository.count();
        long attendedCount = attendanceRepository.findByStatus("present").size();
        int attendanceRate = totalAttendance > 0 ? (int) (attendedCount * 100 / totalAttendance) : 0;
        
        statistics.put("courseCount", courseCount);
        statistics.put("studentCount", studentCount);
        statistics.put("attendanceRate", attendanceRate);
        statistics.put("pendingLeave", pendingLeave);
        
        return Result.success(statistics);
    }
}
