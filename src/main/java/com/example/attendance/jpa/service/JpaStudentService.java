package com.example.attendance.jpa.service;

import com.example.attendance.common.Result;
import com.example.attendance.jpa.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface JpaStudentService {
    Result<Void> addStudent(Student student);

    Page<Student> listStudents(String keyword, Pageable pageable);

    Result<Void> batchDelete(List<Long> ids);

    // 新增编辑、单删、根据id查询
    Student getById(Long id);

    Result<Void> updateStudent(Student student);

    Result<Void> deleteById(Long id);
}
