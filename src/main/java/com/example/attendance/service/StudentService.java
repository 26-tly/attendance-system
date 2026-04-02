package com.example.attendance.service;
import com.example.attendance.entity.Student;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

public abstract class StudentService {


    public abstract String courses();



    public abstract List<Student> getStudentList(
            @RequestParam("className") String className,
            @RequestParam(value = "page", defaultValue = "1") Integer page);



    public abstract Student getStudentInfo(String studentId);



    public abstract Student getStudentById(String studentId);



    public abstract String createStudent(Student student);
}
