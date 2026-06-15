package com.example.attendance.jpa.controller;

import com.example.attendance.common.Result;
import com.example.attendance.jpa.service.AttendanceManagementService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance-management")
public class AttendanceManagementController {

    private final AttendanceManagementService attendanceManagementService;

    public AttendanceManagementController(AttendanceManagementService attendanceManagementService) {
        this.attendanceManagementService = attendanceManagementService;
    }

    @GetMapping("/status")
    public ResponseEntity<Result<Map<String, Object>>> getAttendanceStatus(
            @RequestParam Integer courseId,
            @RequestParam String date) {
        LocalDate attendanceDate = LocalDate.parse(date);
        return ResponseEntity.ok(attendanceManagementService.getAttendanceStatus(courseId, attendanceDate));
    }

    @PostMapping("/makes-up")
    public ResponseEntity<Result<Map<String, Object>>> makeUpAttendance(@RequestBody Map<String, Object> request) {
        Long studentId = getLong(request, "studentId");
        Integer courseId = getInteger(request, "courseId");
        String dateStr = (String) request.get("date");
        LocalDate date = LocalDate.parse(dateStr);
        String reason = (String) request.get("reason");
        Integer operatorId = getInteger(request, "operatorId");
        
        return ResponseEntity.ok(attendanceManagementService.makeUpAttendance(
                studentId, courseId, date, reason, operatorId));
    }

    @PutMapping("/update-status")
    public ResponseEntity<Result<Map<String, Object>>> updateAttendanceStatus(@RequestBody Map<String, Object> request) {
        Integer attendanceId = getInteger(request, "attendanceId");
        String newStatus = (String) request.get("newStatus");
        String reason = (String) request.get("reason");
        Integer operatorId = getInteger(request, "operatorId");
        
        return ResponseEntity.ok(attendanceManagementService.updateAttendanceStatus(
                attendanceId, newStatus, reason, operatorId));
    }

    @GetMapping("/history")
    public ResponseEntity<Result<Page<Map<String, Object>>>> getAttendanceHistory(
            @RequestParam(required = false) Integer courseId,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String studentNo,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : null;
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : null;
        Pageable pageable = PageRequest.of(page, size);
        
        return ResponseEntity.ok(attendanceManagementService.getAttendanceHistory(
                courseId, studentId, userId, studentNo, status, start, end, pageable));
    }

    @GetMapping("/logs")
    public ResponseEntity<Result<Page<Map<String, Object>>>> getAttendanceLogs(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Integer courseId,
            @RequestParam(required = false) Integer operatorId,
            @RequestParam(required = false) String actionType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(attendanceManagementService.getAttendanceLogs(
                studentId, courseId, operatorId, actionType, pageable));
    }

    @GetMapping("/summary")
    public ResponseEntity<Result<Map<String, Object>>> getStudentAttendanceSummary(
            @RequestParam Long studentId,
            @RequestParam(required = false) Integer courseId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : LocalDate.now().minusMonths(1);
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : LocalDate.now();
        
        return ResponseEntity.ok(attendanceManagementService.getStudentAttendanceSummary(
                studentId, courseId, start, end));
    }

    private Integer getInteger(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof String) {
            return Integer.parseInt((String) value);
        }
        return null;
    }

    private Long getLong(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Long) {
            return (Long) value;
        } else if (value instanceof Integer) {
            return ((Integer) value).longValue();
        } else if (value instanceof String) {
            return Long.parseLong((String) value);
        }
        return null;
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportAttendanceHistory(
            @RequestParam(required = false) Integer courseId,
            @RequestParam(required = false) String studentNo,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) throws IOException {
        
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : null;
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : null;
        
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
        Result<Page<Map<String, Object>>> result = attendanceManagementService.getAttendanceHistory(
                courseId, null, null, studentNo, status, start, end, pageable);
        
        List<Map<String, Object>> data = result.getData().getContent();
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("签到历史");
            
            Row headerRow = sheet.createRow(0);
            String[] headerTitles = {"日期", "课程", "学号", "姓名", "签到状态", "签到时间", "是否补签", "补签原因"};
            for (int i = 0; i < headerTitles.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headerTitles[i]);
            }
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter datetimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            
            int rowNum = 1;
            for (Map<String, Object> record : data) {
                Row row = sheet.createRow(rowNum++);
                
                String attendanceDate = record.get("attendanceDate") != null ? 
                        LocalDate.parse(record.get("attendanceDate").toString()).format(formatter) : "-";
                row.createCell(0).setCellValue(attendanceDate);
                
                row.createCell(1).setCellValue((String) record.get("courseName"));
                row.createCell(2).setCellValue((String) record.get("studentNo"));
                row.createCell(3).setCellValue((String) record.get("studentName"));
                
                String statusText = getStatusText((String) record.get("status"));
                row.createCell(4).setCellValue(statusText);
                
                String checkinTime = record.get("checkinTime") != null ? record.get("checkinTime").toString() : "-";
                row.createCell(5).setCellValue(checkinTime);
                
                String isMakesUp = record.get("isMakesUp") != null && (Boolean) record.get("isMakesUp") ? "是" : "否";
                row.createCell(6).setCellValue(isMakesUp);
                
                String reason = (String) record.get("makesUpReason");
                row.createCell(7).setCellValue(reason != null ? reason : "-");
            }
            
            for (int i = 0; i < headerTitles.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            
            String filename = "签到历史_" + LocalDate.now().format(formatter) + ".xlsx";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", filename);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(outputStream.toByteArray());
        }
    }
    
    private String getStatusText(String status) {
        if (status == null) return "未知";
        switch (status) {
            case "present": return "已签到";
            case "absent": return "未签到";
            case "late": return "迟到";
            case "leave": return "请假";
            default: return "未知";
        }
    }
}
