package com.example.attendance.controller;

import com.example.attendance.common.Result;
import com.example.attendance.entity.Attendance;
import com.example.attendance.entity.Student;
import com.example.attendance.jpa.repository.StudentRepository;
import com.example.attendance.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private StudentService studentService;
    
    @Autowired
    private StudentRepository studentRepository;

    @GetMapping("/student/info/{studentId}")
    public Result<Student> getStudentInfo(@PathVariable("studentId") String studentId) {
        return Result.success(studentService.getStudentInfo(studentId));
    }

    @GetMapping("/students")
    public Result<List<Student>> getStudentList(
            @RequestParam("className") String className,
            @RequestParam(value = "page", defaultValue = "1") Integer page) {
        return Result.success(studentService.getStudentList(className, page));
    }

    @PostMapping("/attendance/update")
    public Result<String> updateAttendance(@RequestBody Attendance attendance) {
        System.out.println("更新考勤:" + attendance);
        return Result.success("更新成功");
    }

    @GetMapping("/student/info")
    public String studentInfo() {
        return "姓名：田龙羽，学号：42411120，班级：计科3班";
    }

    @PostMapping("/student/attendence")
    public String attendence() {
        String p = "42411120";
        return "学号" + p + "的学生打卡成功";
    }

    @GetMapping("/student/courses")
    public Result<String> courses() {
        return Result.success(studentService.courses());
    }

    @PostMapping("/create")
    public Result<String> create(@RequestBody Student student) {
        return Result.success(studentService.createStudent(student));
    }

    @GetMapping("/{id}")
    public Result<Student> getStudentById(@PathVariable("id") String id) {
        return Result.success(studentService.getStudentById(id));
    }

    @GetMapping("/list")
    public Result<List<Object>> getAllStudents() {
        List<Object> result = new java.util.ArrayList<>();
        studentRepository.findAll().forEach(s -> {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", s.getId());
            map.put("studentNo", s.getStudentNo());
            map.put("name", s.getName());
            map.put("gender", s.getGender());
            map.put("phone", s.getPhone());
            map.put("birthDate", s.getBirthDate());
            result.add(map);
        });
        return Result.success(result);
    }

}