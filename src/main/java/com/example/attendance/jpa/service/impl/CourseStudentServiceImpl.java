package com.example.attendance.jpa.service.impl;

import com.example.attendance.common.Result;
import com.example.attendance.jpa.entity.Course;
import com.example.attendance.jpa.entity.CourseStudent;
import com.example.attendance.jpa.entity.Student;
import com.example.attendance.jpa.repository.CourseRepository;
import com.example.attendance.jpa.repository.CourseStudentRepository;
import com.example.attendance.jpa.repository.StudentRepository;
import com.example.attendance.jpa.service.CourseStudentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CourseStudentServiceImpl implements CourseStudentService {

    private final CourseStudentRepository courseStudentRepository;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;

    public CourseStudentServiceImpl(CourseStudentRepository courseStudentRepository,
                                   CourseRepository courseRepository,
                                   StudentRepository studentRepository) {
        this.courseStudentRepository = courseStudentRepository;
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    public Result<List<Map<String, Object>>> getStudentsByCourseId(Integer courseId) {
        if (courseId == null) {
            return Result.error("课程ID不能为空");
        }

        Optional<Course> courseOpt = courseRepository.findById(courseId);
        if (courseOpt.isEmpty()) {
            return Result.error("课程不存在");
        }

        List<CourseStudent> courseStudents = courseStudentRepository.findByCourseId(courseId);
        List<Long> studentIds = courseStudents.stream()
                .map(CourseStudent::getStudentId)
                .collect(Collectors.toList());

        if (studentIds.isEmpty()) {
            return Result.success(new ArrayList<>());
        }

        List<Student> students = studentRepository.findAllById(studentIds);
        List<Map<String, Object>> result = students.stream()
                .map(student -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", student.getId());
                    map.put("studentNo", student.getStudentNo());
                    map.put("name", student.getName());
                    map.put("gender", student.getGender());
                    map.put("phone", student.getPhone());
                    map.put("birthDate", student.getBirthDate());
                    return map;
                })
                .collect(Collectors.toList());

        return Result.success(result);
    }

    @Override
    public Result<List<Map<String, Object>>> getCoursesByStudentId(Long studentId) {
        if (studentId == null) {
            return Result.error("学生ID不能为空");
        }

        List<CourseStudent> courseStudents = courseStudentRepository.findByStudentId(studentId);
        List<Integer> courseIds = courseStudents.stream()
                .map(CourseStudent::getCourseId)
                .collect(Collectors.toList());

        if (courseIds.isEmpty()) {
            return Result.success(new ArrayList<>());
        }

        List<Course> courses = courseRepository.findAllById(courseIds);
        List<Map<String, Object>> result = courses.stream()
                .map(course -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("courseId", course.getCourseId());
                    map.put("courseName", course.getCourseName());
                    map.put("classroom", course.getClassroom());
                    map.put("startTime", course.getStartTime());
                    map.put("endTime", course.getEndTime());
                    return map;
                })
                .collect(Collectors.toList());

        return Result.success(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> addStudentToCourse(Integer courseId, Long studentId) {
        if (courseId == null || studentId == null) {
            return Result.error("课程ID和学生ID不能为空");
        }

        if (courseStudentRepository.existsByCourseIdAndStudentId(courseId, studentId)) {
            return Result.error("该学生已在课程中");
        }

        CourseStudent courseStudent = new CourseStudent(courseId, studentId);
        courseStudentRepository.save(courseStudent);

        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> removeStudentFromCourse(Integer courseId, Long studentId) {
        if (courseId == null || studentId == null) {
            return Result.error("课程ID和学生ID不能为空");
        }

        Optional<CourseStudent> courseStudentOpt = 
                courseStudentRepository.findByCourseIdAndStudentId(courseId, studentId);
        
        if (courseStudentOpt.isEmpty()) {
            return Result.error("该学生不在此课程中");
        }

        courseStudentRepository.delete(courseStudentOpt.get());
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Map<String, Object>> batchAddStudentsToCourse(Integer courseId, List<Long> studentIds) {
        if (courseId == null || studentIds == null || studentIds.isEmpty()) {
            return Result.error("课程ID和学生ID列表不能为空");
        }

        int successCount = 0;
        for (Long studentId : studentIds) {
            if (!courseStudentRepository.existsByCourseIdAndStudentId(courseId, studentId)) {
                CourseStudent courseStudent = new CourseStudent(courseId, studentId);
                courseStudentRepository.save(courseStudent);
                successCount++;
            }
        }

        Map<String, Object> data = new HashMap<>();
        data.put("successCount", successCount);
        data.put("totalCount", studentIds.size());
        
        return Result.success(data);
    }

    @Override
    public Result<Map<String, Object>> getCourseStudentInfo(Integer courseId) {
        if (courseId == null) {
            return Result.error("课程ID不能为空");
        }

        Optional<Course> courseOpt = courseRepository.findById(courseId);
        if (courseOpt.isEmpty()) {
            return Result.error("课程不存在");
        }

        Course course = courseOpt.get();
        List<CourseStudent> courseStudents = courseStudentRepository.findByCourseId(courseId);

        Map<String, Object> result = new HashMap<>();
        result.put("courseId", course.getCourseId());
        result.put("courseName", course.getCourseName());
        result.put("classroom", course.getClassroom());
        result.put("startTime", course.getStartTime());
        result.put("endTime", course.getEndTime());
        result.put("studentCount", courseStudents.size());

        return Result.success(result);
    }
}
