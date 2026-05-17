package com.example.attendance.jpa.service.impl;

import com.example.attendance.common.Result;
import com.example.attendance.jpa.entity.Student;
import com.example.attendance.jpa.repository.StudentRepository;
import com.example.attendance.jpa.service.JpaStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JpaStudentServiceImpl implements JpaStudentService {
    @Autowired
    private StudentRepository studentRepository;
    @Override
    public Result<Void> addStudent(Student student) {
        studentRepository.save(student);
        return Result.success();
    }

    @Override
    public Page<Student> listStudents(String keyword, Pageable pageable) {
        if (keyword == null || keyword.isBlank()) {
            return studentRepository.findAll(pageable);
        }
        return studentRepository.findByStudentNoContainingOrNameContaining(keyword, keyword, pageable);
    }

    @Override
    public Result<Void> batchDelete(List<Long> ids) {
        studentRepository.deleteAllByIdIn(ids);
        return Result.success();
    }

    @Override
    public Student getById(Long id) {
        return studentRepository.findById(id).orElse(null);
    }

    @Override
    public Result<Void> updateStudent(Student student) {
        if (!studentRepository.existsById(student.getId())) {
            return Result.error("学生信息不存在");
        }
        studentRepository.save(student);
        return Result.success();
    }

    @Override
    public Result<Void> deleteById(Long id) {
        studentRepository.deleteById(id);
        return Result.success();
    }
}
