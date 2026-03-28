package com.example.attendance.dao;

import com.example.attendance.entity.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


@Repository

public class StudentDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    public void insert(Student student) {

    }
    public Student findById(String studentId) {

        return null;
    }

}
