package com.example.attendance.controller;
import  com.example.attendance.common.Result;
import com.example.attendance.entity.Attendance;
import com.example.attendance.entity.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.attendance.service.StudentService;

import java.util.List;

@RestController

public class StudentController {
    @GetMapping("/student/info/{studentId}")
    public Result<Student> getStudentInfo(@PathVariable ("studentId") String studentId) {
        return Result.success(studentService.getStudentInfo(studentId));
    }

    @GetMapping("/students")
    public Result<List<Student>> getStudentList(
            @RequestParam("className") String className,
            @RequestParam(value = "page",defaultValue = "1") Integer page){
        return Result.success(studentService.getStudentList(className,page));
    }
    @PostMapping("/attendance/update")
    public Result<String> updateAttendance(@RequestBody Attendance attendance){
        System.out.println("更新考勤:"+attendance);
        return Result.success("更新成功");
    }


    @GetMapping("/student/info")
    public String studentInfo() {
        return"姓名：田龙羽，学号：42411120，班级：计科3班";
    }
    @PostMapping("/student/attendence")
    public String attendence() {
        String p="42411120";
        return "学号"+p+"的学生打卡成功";
    }
    @GetMapping("/student/courses")
    public Result<String> courses() {
        return Result.success(studentService.courses());
    }
    @Autowired
    private StudentService studentService;
    @PostMapping("/create")
    public Result<String> create(@RequestBody Student student){
        return Result.success(studentService.createStudent(student));
    }
    @GetMapping("/{id}")//{}表示id是动态参数
    public Result<Student> getStudentById(@PathVariable("id") String id){
        return Result.success(studentService.getStudentById(id));
    }



}
