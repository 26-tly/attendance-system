package com.example.attendance.jpa.controller;

import com.example.attendance.common.Result;
import com.example.attendance.jpa.config.FileUploadConfig;
import com.example.attendance.jpa.entity.Attendance;
import com.example.attendance.jpa.repository.AttendanceRepository;
import com.example.attendance.jpa.util.ExcelUtil;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/excel")
public class ExcelController {
    private final FileUploadConfig config;
    private final AttendanceRepository repository;

    public ExcelController(FileUploadConfig config, AttendanceRepository repository) {
        this.config = config;
        this.repository = repository;
    }

    @PostMapping("/upload")
    public Result<Map<String, Object>> upload(@RequestParam("file") MultipartFile file) throws Exception {
        if (file.isEmpty()) return Result.error("璇烽€夋嫨鏂囦欢");

        String name = file.getOriginalFilename();
        String suffix = name.substring(name.lastIndexOf("."));
        if (!config.getAllowSuffixList().contains(suffix)) {
            return Result.error("浠呮敮鎸?xls/.xlsx");
        }

        double mb = file.getSize() / 1024.0 / 1024.0;
        if (mb > 100) return Result.error("瓒呰繃100MB");

        File dir = new File(config.getUploadPath());
        if (!dir.exists()) dir.mkdirs();
        String newName = UUID.randomUUID() + suffix;
        file.transferTo(new File(dir, newName));

        var workbook = WorkbookFactory.create(file.getInputStream());
        var map = ExcelUtil.readExcel(workbook);
        workbook.close();

        var successList = (List<Attendance>) map.get("success");
        if (!successList.isEmpty()) {
            repository.saveAll(successList);
        }

        return Result.success(map);
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportAttendance(
            @RequestParam(required = false) Integer courseId,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) throws Exception {

        List<Attendance> attendanceList;
        if (courseId != null) {
            attendanceList = repository.findByCourseId(courseId);
        } else if (userId != null) {
            attendanceList = repository.findByUserId(userId);
        } else if (status != null) {
            attendanceList = repository.findByStatus(status);
        } else {
            attendanceList = repository.findAll();
        }

        byte[] excelData = ExcelUtil.exportAttendanceToExcel(attendanceList);  

        String filename = "鑰冨嫟璁板綍_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx";
        String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", encodedFilename);  
        headers.add("Access-Control-Expose-Headers", "Content-Disposition");   

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelData);
    }
}