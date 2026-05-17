package com.example.attendance.jpa.repository;

import com.example.attendance.jpa.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student,Long> {
    // 搜索+排序
    Page<Student> findByStudentNoContainingOrNameContaining(String studentNo, String name, Pageable pageable);
    // 批量删除
    void deleteAllByIdIn(List<Long> ids);
}

