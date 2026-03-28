package com.example.attendance.service.impl;
import com.example.attendance.entity.Student;
import com.example.attendance.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.attendance.dao.StudentDao;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;


@Service


public class StudentServiceImpl extends StudentService {
    @Autowired
    private StudentService studentService;
    private StudentDao studentDao;
    private Student student;

    @Override
    public String courses(){
        List<String> courses=new ArrayList<>();
        courses.add("javaEE开发实践");
        courses.add("大学物理实验");
        courses.add("机械学习与数据挖掘");
        return courses.toString();
    }
    @Override
    public List<Student> getStudentList(
            @RequestParam("className") String className,
            @RequestParam(value = "page", defaultValue = "1") Integer page){
        List<Student> students = new ArrayList<>();
        students.add(new Student("田龙羽", "42411120"));
        students.add(new Student("敬凌杰", "42411026"));
        return students;
    }
    @Override
    public Student getStudentInfo(String studentId){
        Student student = new Student();
        student.setStudentId("42411120");
        student.setName("田龙羽");
        student.setAge(20);
        student.setGender("男");
        return student;
    }

    @Override
    public Student getStudentById(String studentId) {
        return studentDao.findById(studentId);
    }

    @Override
    public String createStudent(Student student) {
        if(student.getName() == null || student.getName().isEmpty()){
            throw new RuntimeException("姓名不能为空");
        }
        studentDao.insert(student);
        return "创建成功";
    }

}
