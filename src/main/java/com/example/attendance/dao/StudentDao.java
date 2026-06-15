package com.example.attendance.dao;

import com.example.attendance.entity.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;


@Repository

public class StudentDao {
    @Autowired
    private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    public void insert(Student student) {

    }
    public Student findById(String studentId) {

        return null;

    }

    public List<Student> findAll() {
        return new ArrayList<>();
    }

}
