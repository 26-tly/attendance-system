package com.example.attendance.jpa.service;

import com.example.attendance.common.Result;
import com.example.attendance.jpa.entity.CourseStudent;
import com.example.attendance.jpa.entity.Student;

import java.util.List;
import java.util.Map;

public interface CourseStudentService {
    
    Result<List<Map<String, Object>>> getStudentsByCourseId(Integer courseId);
    
    Result<List<Map<String, Object>>> getCoursesByStudentId(Long studentId);
    
    Result<Void> addStudentToCourse(Integer courseId, Long studentId);
    
    Result<Void> removeStudentFromCourse(Integer courseId, Long studentId);
    
    Result<Map<String, Object>> batchAddStudentsToCourse(Integer courseId, List<Long> studentIds);
    
    Result<Map<String, Object>> getCourseStudentInfo(Integer courseId);
}
