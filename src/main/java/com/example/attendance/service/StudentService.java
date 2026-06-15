package com.example.attendance.service;
import com.example.attendance.entity.Student;

import java.util.List;

public interface StudentService {

    String courses();

    List<Student> getStudentList(String className, Integer page);



    public abstract Student getStudentInfo(String studentId);



    public abstract Student getStudentById(String studentId);



    public abstract String createStudent(Student student);
}
