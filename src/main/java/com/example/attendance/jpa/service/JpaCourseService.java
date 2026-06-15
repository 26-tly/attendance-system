package com.example.attendance.jpa.service;

import com.example.attendance.common.Result;
import com.example.attendance.jpa.entity.Course;
import org.springframework.data.domain.Page;

import java.util.List;

public interface JpaCourseService {
    Result<Course> addCourse(Course course);

    Result<Course> updateCourse(Course course);

    Result<Void> deleteCourse(Integer courseId);

    Result<Integer> batchDeleteCourses(List<Integer> courseIds);

    Course getById(Integer courseId);

    List<Course> getAllCourses();

    Page<Course> getCourses(int pageNum, int pageSize);

    Course saveCourse(Course course);
}