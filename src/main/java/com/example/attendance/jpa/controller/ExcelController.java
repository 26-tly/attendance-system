package com.example.attendance.jpa.controller;

import com.example.attendance.common.Result;
import com.example.attendance.jpa.config.FileUploadConfig;
import com.example.attendance.jpa.entity.Attendance;
import com.example.attendance.jpa.repository.AttendanceRepository;
import com.example.attendance.jpa.util.ExcelUtil;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/excel")
@RequiredArgsConstructor
public class ExcelController {
    private final FileUploadConfig config;
    private final AttendanceRepository repository;

    @PostMapping("/upload")
    public Result<Map<String, Object>> upload(@RequestParam("file") MultipartFile file) throws Exception {
        // 1 非空
        if (file.isEmpty()) return Result.error("请选择文件");

        // 2 后缀
        String name = file.getOriginalFilename();
        String suffix = name.substring(name.lastIndexOf("."));
        if (!config.getAllowSuffixList().contains(suffix)) {
            return Result.error("仅支持.xls/.xlsx");
        }

        // 3 大小
        double mb = file.getSize() / 1024.0 / 1024.0;
        if (mb > 100) return Result.error("超过100MB");

        // 4 保存文件
        File dir = new File(config.getUploadPath());
        if (!dir.exists()) dir.mkdirs();
        String newName = UUID.randomUUID() + suffix;
        file.transferTo(new File(dir, newName));

        // 5 解析Excel
        var workbook = WorkbookFactory.create(file.getInputStream());
        var map = ExcelUtil.readExcel(workbook);
        workbook.close();

        // 6 批量保存数据库
        var successList = (List<Attendance>) map.get("success");
        if (!successList.isEmpty()) {
            repository.saveAll(successList);
        }

        return Result.success(map);
    }
}
