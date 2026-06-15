package com.example.attendance.jpa.service.impl;

import com.example.attendance.common.Result;
import com.example.attendance.jpa.entity.Student;
import com.example.attendance.jpa.entity.User;
import com.example.attendance.jpa.repository.AttendanceRepository;
import com.example.attendance.jpa.repository.CourseStudentRepository;
import com.example.attendance.jpa.repository.StudentRepository;
import com.example.attendance.jpa.repository.UserRepository;
import com.example.attendance.jpa.service.JpaStudentService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JpaStudentServiceImpl implements JpaStudentService {
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseStudentRepository courseStudentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PersistenceContext
    private EntityManager entityManager;
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
    @Transactional(rollbackFor = Exception.class)
    public Result<Map<String, Object>> batchDelete(List<Long> ids) {
        Map<String, Object> result = new HashMap<>();
        int successCount = 0;
        int failedCount = 0;
        StringBuilder errorMessages = new StringBuilder();

        if (ids == null || ids.isEmpty()) {
            result.put("successCount", 0);
            result.put("failedCount", 0);
            result.put("message", "未选择任何学生");
            return Result.error("未选择任何学生", result);
        }

        for (Long id : ids) {
            try {
                attendanceRepository.deleteByUserId(id.intValue());
                studentRepository.deleteById(id);
                successCount++;
            } catch (Exception e) {
                failedCount++;
                if (errorMessages.length() > 0) {
                    errorMessages.append("; ");
                }
                errorMessages.append("ID ").append(id).append(": ").append(e.getMessage());
            }
        }

        result.put("successCount", successCount);
        result.put("failedCount", failedCount);
        if (failedCount > 0) {
            result.put("message", errorMessages.length() < 500 ? errorMessages.toString() : "部分删除失败");
            return Result.error("部分删除失败", result);
        } else {
            result.put("message", "全部删除成功");
            return Result.success(result);
        }
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
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> deleteById(Long id) {
        attendanceRepository.deleteByUserId(id.intValue());
        studentRepository.deleteById(id);
        return Result.success();
    }

    @Override
    public Student saveStudent(Student student) {
        return studentRepository.save(student);
    }

    @Override
    public Result<Map<String, Object>> importStudents(MultipartFile file, Integer courseId) {
        Map<String, Object> result = new HashMap<>();
        int successCount = 0;
        int failedCount = 0;
        StringBuilder errorMessages = new StringBuilder();
        String filename = file.getOriginalFilename();

        System.out.println("========== 开始导入学生数据 ==========");
        System.out.println("文件名: " + filename);
        System.out.println("文件大小: " + file.getSize() + " bytes");
        System.out.println("课程ID: " + courseId);

        try {
            if (filename != null && filename.toLowerCase().endsWith(".csv")) {
                System.out.println("检测到CSV文件，开始导入...");
                return importFromCsv(file, result, courseId);
            } else if (filename != null && filename.toLowerCase().endsWith(".xlsx")) {
                System.out.println("检测到XLSX文件，开始导入...");
                return importFromExcel(new XSSFWorkbook(file.getInputStream()), result, courseId);
            } else if (filename != null && filename.toLowerCase().endsWith(".xls")) {
                System.out.println("检测到XLS文件，开始导入...");
                return importFromExcel(new HSSFWorkbook(file.getInputStream()), result, courseId);
            } else {
                System.out.println("文件格式不正确: " + filename);
                result.put("successCount", 0);
                result.put("failedCount", 0);
                result.put("message", "文件格式不正确，请上传.csv、.xlsx或.xls格式的文件");
                return Result.success(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("successCount", 0);
            result.put("failedCount", 0);
            result.put("message", "读取文件失败: " + e.getMessage());
            return Result.success(result);
        }
    }

    private Result<Map<String, Object>> importFromCsv(MultipartFile file, Map<String, Object> result, Integer courseId) {
        int successCount = 0;
        int failedCount = 0;
        StringBuilder errorMessages = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            int lineNum = 0;
            
            while ((line = reader.readLine()) != null) {
                lineNum++;
                
                if (lineNum == 1) {
                    // 检查首行是否包含表头
                    if (!line.contains("学号") || !line.contains("姓名")) {
                        // 尝试用GBK编码重新读取
                        return importFromCsvWithEncoding(file, result, courseId, "GBK");
                    }
                    continue;
                }

                if (line.trim().isEmpty()) continue;

                try {
                    String[] parts = line.split(",");
                    if (parts.length < 2) continue;

                    String studentNo = parts[0].trim();
                    String studentName = parts[1].trim();

                    // 检查学生是否已存在
                    List<Student> existingStudents = studentRepository.findByStudentNo(studentNo);
                    Student savedStudent;
                    if (!existingStudents.isEmpty()) {
                        // 更新现有学生信息
                        Student existingStudent = existingStudents.get(0);
                        existingStudent.setName(studentName);
                        existingStudent.setGender(parts.length > 2 ? parts[2].trim() : "");
                        existingStudent.setPhone(parts.length > 3 ? parts[3].trim() : "");
                        savedStudent = studentRepository.save(existingStudent);
                    } else {
                        // 创建新学生
                        Student student = new Student();
                        student.setStudentNo(studentNo);
                        student.setName(studentName);
                        student.setGender(parts.length > 2 ? parts[2].trim() : "");
                        student.setPhone(parts.length > 3 ? parts[3].trim() : "");
                        savedStudent = studentRepository.save(student);
                    }

                    // 检查用户是否已存在
                    User existingUser = userRepository.findByUsername(studentNo);
                    if (existingUser == null) {
                        // 同时在User表中创建对应的用户账户
                        User user = new User();
                        user.setUsername(studentNo); // 使用学号作为用户名
                        user.setPassword(passwordEncoder.encode(studentNo)); // 初始密码为学号（加密存储）
                        user.setUserRole("student");
                        userRepository.save(user);
                    }

                    // 如果指定了课程ID，将学生添加到课程中
                    if (courseId != null) {
                        // 检查学生是否已在该课程中
                        if (!courseStudentRepository.existsByCourseIdAndStudentId(courseId, savedStudent.getId())) {
                            com.example.attendance.jpa.entity.CourseStudent courseStudent = new com.example.attendance.jpa.entity.CourseStudent(courseId, savedStudent.getId());
                            courseStudentRepository.save(courseStudent);
                        }
                    }

                    successCount++;
                } catch (Exception e) {
                    failedCount++;
                    e.printStackTrace();
                    if (errorMessages.length() > 0) {
                        errorMessages.append("; ");
                    }
                    errorMessages.append("第").append(lineNum).append("行导入失败: ").append(e.getMessage());
                }
            }
        } catch (Exception e) {
            result.put("successCount", 0);
            result.put("failedCount", 0);
            result.put("message", "读取CSV文件失败: " + e.getMessage());
            return Result.success(result);
        }

        result.put("successCount", successCount);
        result.put("failedCount", failedCount);
        if (errorMessages.length() > 0 && errorMessages.length() < 500) {
            result.put("message", errorMessages.toString());
        } else if (errorMessages.length() >= 500) {
            result.put("message", "部分记录导入失败，详情请查看日志");
        }

        return Result.success(result);
    }

    private Result<Map<String, Object>> importFromCsvWithEncoding(MultipartFile file, Map<String, Object> result, Integer courseId, String encoding) {
        int successCount = 0;
        int failedCount = 0;
        StringBuilder errorMessages = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), encoding))) {
            String line;
            int lineNum = 0;
            
            while ((line = reader.readLine()) != null) {
                lineNum++;
                if (lineNum == 1) continue;

                if (line.trim().isEmpty()) continue;

                try {
                    String[] parts = line.split(",");
                    if (parts.length < 2) continue;

                    String studentNo = parts[0].trim();
                    String studentName = parts[1].trim();

                    List<Student> existingStudents = studentRepository.findByStudentNo(studentNo);
                    Student savedStudent;
                    if (!existingStudents.isEmpty()) {
                        Student existingStudent = existingStudents.get(0);
                        existingStudent.setName(studentName);
                        existingStudent.setGender(parts.length > 2 ? parts[2].trim() : "");
                        existingStudent.setPhone(parts.length > 3 ? parts[3].trim() : "");
                        savedStudent = studentRepository.save(existingStudent);
                    } else {
                        Student student = new Student();
                        student.setStudentNo(studentNo);
                        student.setName(studentName);
                        student.setGender(parts.length > 2 ? parts[2].trim() : "");
                        student.setPhone(parts.length > 3 ? parts[3].trim() : "");
                        savedStudent = studentRepository.save(student);
                    }

                    User existingUser = userRepository.findByUsername(studentNo);
                    if (existingUser == null) {
                        User user = new User();
                        user.setUsername(studentNo);
                        user.setPassword(passwordEncoder.encode(studentNo)); // 加密存储
                        user.setUserRole("student");
                        userRepository.save(user);
                    }

                    if (courseId != null) {
                        if (!courseStudentRepository.existsByCourseIdAndStudentId(courseId, savedStudent.getId())) {
                            com.example.attendance.jpa.entity.CourseStudent courseStudent = new com.example.attendance.jpa.entity.CourseStudent(courseId, savedStudent.getId());
                            courseStudentRepository.save(courseStudent);
                        }
                    }

                    successCount++;
                } catch (Exception e) {
                    failedCount++;
                    e.printStackTrace();
                    if (errorMessages.length() > 0) {
                        errorMessages.append("; ");
                    }
                    errorMessages.append("第").append(lineNum).append("行导入失败: ").append(e.getMessage());
                }
            }
        } catch (Exception e) {
            result.put("successCount", 0);
            result.put("failedCount", 0);
            result.put("message", "读取CSV文件失败: " + e.getMessage());
            return Result.success(result);
        }

        result.put("successCount", successCount);
        result.put("failedCount", failedCount);
        if (errorMessages.length() > 0 && errorMessages.length() < 500) {
            result.put("message", errorMessages.toString());
        } else if (errorMessages.length() >= 500) {
            result.put("message", "部分记录导入失败，详情请查看日志");
        }

        return Result.success(result);
    }

    private Result<Map<String, Object>> importFromExcel(Workbook workbook, Map<String, Object> result, Integer courseId) {
        int successCount = 0;
        int failedCount = 0;
        StringBuilder errorMessages = new StringBuilder();

        System.out.println("========== 开始Excel导入 ==========");
        System.out.println("课程ID: " + courseId);

        try {
            Sheet sheet = workbook.getSheetAt(0);
            int rowCount = sheet.getPhysicalNumberOfRows();
            System.out.println("Excel总行数: " + rowCount);
            
            for (int i = 1; i < rowCount; i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    System.out.println("第" + (i+1) + "行为空，跳过");
                    continue;
                }

                try {
                    Student student = new Student();
                    
                    Cell studentNoCell = row.getCell(0);
                    Cell nameCell = row.getCell(1);
                    Cell genderCell = row.getCell(2);
                    Cell phoneCell = row.getCell(3);

                    if (studentNoCell == null || getCellValue(studentNoCell).trim().isEmpty()) {
                        System.out.println("第" + (i+1) + "行学号为空，跳过");
                        continue;
                    }

                    String studentNo = getCellValue(studentNoCell);
                    String studentName = getCellValue(nameCell);
                    System.out.println("解析第" + (i+1) + "行: 学号=" + studentNo + ", 姓名=" + studentName);

                    // 检查学生是否已存在
                    List<Student> existingStudents = studentRepository.findByStudentNo(studentNo);
                    Student savedStudent;
                    if (!existingStudents.isEmpty()) {
                        // 更新现有学生信息
                        System.out.println("学生已存在，更新信息: " + studentNo);
                        Student existingStudent = existingStudents.get(0);
                        existingStudent.setName(studentName);
                        existingStudent.setGender(getCellValue(genderCell));
                        existingStudent.setPhone(getCellValue(phoneCell));
                        savedStudent = studentRepository.save(existingStudent);
                    } else {
                        // 创建新学生
                        System.out.println("创建新学生: " + studentNo);
                        student.setStudentNo(studentNo);
                        student.setName(studentName);
                        student.setGender(getCellValue(genderCell));
                        student.setPhone(getCellValue(phoneCell));
                        savedStudent = studentRepository.save(student);
                    }
                    System.out.println("学生保存成功，ID: " + savedStudent.getId());

                    // 检查用户是否已存在
                    User existingUser = userRepository.findByUsername(studentNo);
                    if (existingUser == null) {
                        // 同时在User表中创建对应的用户账户
                        System.out.println("创建用户账户: " + studentNo);
                        User user = new User();
                        user.setUsername(studentNo); // 使用学号作为用户名
                        user.setPassword(passwordEncoder.encode(studentNo)); // 初始密码为学号（加密存储）
                        user.setUserRole("student");
                        userRepository.save(user);
                    }

                    // 如果指定了课程ID，将学生添加到课程中
                    if (courseId != null) {
                        // 检查学生是否已在该课程中
                        boolean exists = courseStudentRepository.existsByCourseIdAndStudentId(courseId, savedStudent.getId());
                        System.out.println("检查课程关联: courseId=" + courseId + ", studentId=" + savedStudent.getId() + ", 已存在=" + exists);
                        if (!exists) {
                            com.example.attendance.jpa.entity.CourseStudent courseStudent = new com.example.attendance.jpa.entity.CourseStudent(courseId, savedStudent.getId());
                            courseStudentRepository.save(courseStudent);
                            System.out.println("课程关联创建成功");
                        }
                    } else {
                        System.out.println("警告: 课程ID为空，未关联课程");
                    }

                    successCount++;
                    System.out.println("第" + (i+1) + "行导入成功");
                } catch (Exception e) {
                    failedCount++;
                    System.err.println("第" + (i+1) + "行导入失败: " + e.getMessage());
                    e.printStackTrace();
                    if (errorMessages.length() > 0) {
                        errorMessages.append("; ");
                    }
                    errorMessages.append("第").append(i + 1).append("行导入失败: ").append(e.getMessage());
                }
            }
        } finally {
            try {
                workbook.close();
            } catch (Exception e) {
                // ignore
            }
        }

        System.out.println("========== 导入完成 ==========");
        System.out.println("成功: " + successCount + " 条，失败: " + failedCount + " 条");

        result.put("successCount", successCount);
        result.put("failedCount", failedCount);
        if (errorMessages.length() > 0 && errorMessages.length() < 500) {
            result.put("message", errorMessages.toString());
        } else if (errorMessages.length() >= 500) {
            result.put("message", "部分记录导入失败，详情请查看日志");
        }

        return Result.success(result);
    }

    private String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toString();
                }
                double value = cell.getNumericCellValue();
                if (value == Math.floor(value)) {
                    return String.valueOf((long) value);
                }
                return String.valueOf(value);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue();
                } catch (Exception e) {
                    return String.valueOf(cell.getNumericCellValue());
                }
            default:
                return "";
        }
    }
}
