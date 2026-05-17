package com.example.attendance.jpa.controller;

import com.example.attendance.common.Result;
import com.example.attendance.jpa.entity.Student;
import com.example.attendance.jpa.service.JpaStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class JpaStudentController {
    @Autowired
    private JpaStudentService jpaStudentService;
    @PostMapping("/add")
    public ResponseEntity<Result<Void>> add(@RequestBody Student student) {
        return ResponseEntity.ok(jpaStudentService.addStudent(student));
    }

    @GetMapping("/list")
    public ResponseEntity<Page<Student>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "studentNo") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Student> data =jpaStudentService.listStudents(keyword, pageable);
        return ResponseEntity.ok(data);
    }

    @DeleteMapping("/batchDelete")
    public ResponseEntity<Result<Void>> batchDelete(@RequestBody List<Long> ids) {
        return ResponseEntity.ok(jpaStudentService.batchDelete(ids));
    }
    // 根据ID查询（编辑回显）
    @GetMapping("/{id}")
    public ResponseEntity<Student> getById(@PathVariable Long id) {
        return ResponseEntity.ok(jpaStudentService.getById(id));
    }

    // 更新学生
    @PutMapping("/update")
    public ResponseEntity<Result<Void>> update(@RequestBody Student student) {
        return ResponseEntity.ok(jpaStudentService.updateStudent(student));
    }

    // 单个删除
    @DeleteMapping("/{id}")
    public ResponseEntity<Result<Void>> delete(@PathVariable Long id) {
        return ResponseEntity.ok(jpaStudentService.deleteById(id));
    }


}
