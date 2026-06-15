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
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student")
public class JpaStudentController {
    @Autowired
    private JpaStudentService jpaStudentService;
    @PostMapping
    public ResponseEntity<Student> add(@RequestBody Student student) {
        return ResponseEntity.ok(jpaStudentService.saveStudent(student));
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
    public ResponseEntity<Result<Map<String, Object>>> batchDelete(@RequestBody List<Long> ids) {
        return ResponseEntity.ok(jpaStudentService.batchDelete(ids));
    }
    // 根据ID查询（编辑回显）
    @GetMapping("/{id}")
    public ResponseEntity<Student> getById(@PathVariable Long id) {
        return ResponseEntity.ok(jpaStudentService.getById(id));
    }

    // 更新学生
    @PutMapping("/{id}")
    public ResponseEntity<Student> update(@PathVariable Long id, @RequestBody Student student) {
        student.setId(id);
        return ResponseEntity.ok(jpaStudentService.saveStudent(student));
    }

    // 单个删除
    @DeleteMapping("/{id}")
    public ResponseEntity<Result<Void>> delete(@PathVariable Long id) {
        return ResponseEntity.ok(jpaStudentService.deleteById(id));
    }

    @PostMapping("/import")
    public ResponseEntity<Result<Map<String, Object>>> importStudents(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "courseId", required = false) Integer courseId) {
        System.out.println("========== 收到导入请求 ==========");
        System.out.println("文件名: " + file.getOriginalFilename());
        System.out.println("文件大小: " + file.getSize() + " bytes");
        System.out.println("课程ID: " + courseId);
        try {
            Result<Map<String, Object>> result = jpaStudentService.importStudents(file, courseId);
            System.out.println("导入结果: " + result);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> data = new HashMap<>();
            data.put("successCount", 0);
            data.put("failedCount", 0);
            data.put("message", "导入失败: " + e.getMessage());
            return ResponseEntity.ok(Result.success(data));
        }
    }

}
