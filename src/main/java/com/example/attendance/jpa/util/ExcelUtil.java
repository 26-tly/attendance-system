package com.example.attendance.jpa.util;

import com.example.attendance.jpa.entity.Attendance;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExcelUtil {
    public static Map<String, Object> readExcel(Workbook workbook) {
        List<Attendance> success = new ArrayList<>();
        List<String> fail = new ArrayList<>();

        Sheet sheet = workbook.getSheetAt(0);
        if (sheet == null) {
            fail.add("无工作表");
            return Map.of("success", success, "fail", fail);
        }

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            try {
                Attendance a = new Attendance();

                // 按列读取：课程ID、用户ID、日期、状态、座位
                a.setCourseId((int) row.getCell(0).getNumericCellValue());
                a.setUserId((int) row.getCell(1).getNumericCellValue());
                a.setAttendanceDate(LocalDate.parse(row.getCell(2).getStringCellValue()));
                a.setStatus(row.getCell(3).getStringCellValue());
                a.setSeatLocation(row.getCell(4) != null ? row.getCell(4).getStringCellValue() : null);

                // 状态校验
                if (!List.of("出勤", "缺勤", "迟到", "早退").contains(a.getStatus())) {
                    throw new RuntimeException("非法状态");
                }

                success.add(a);
            } catch (Exception e) {
                fail.add("第" + (i + 1) + "行：" + e.getMessage());
            }
        }

        return Map.of("success", success, "fail", fail);
    }
}
