package com.example.attendance.jpa.util;

import com.example.attendance.jpa.entity.Attendance;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExcelUtil {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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

                a.setCourseId((int) row.getCell(0).getNumericCellValue());
                a.setUserId((int) row.getCell(1).getNumericCellValue());
                a.setAttendanceDate(LocalDate.parse(row.getCell(2).getStringCellValue()));
                a.setStatus(row.getCell(3).getStringCellValue());
                a.setSeatLocation(row.getCell(4) != null ? row.getCell(4).getStringCellValue() : null);

                if (!List.of("出勤", "缺勤", "迟到", "早退", "present", "absent", "late", "early", "leave").contains(a.getStatus())) {
                    throw new RuntimeException("非法状态");
                }

                success.add(a);
            } catch (Exception e) {
                fail.add("第" + (i + 1) + "行：" + e.getMessage());
            }
        }

        return Map.of("success", success, "fail", fail);
    }

    public static byte[] exportAttendanceToExcel(List<Attendance> attendanceList) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("考勤记录");

            String[] headers = {"考勤ID", "课程ID", "用户ID", "考勤日期", "状态", "座位位置", "签到时间", "签退时间"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                CellStyle style = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setBold(true);
                style.setFont(font);
                style.setAlignment(HorizontalAlignment.CENTER);
                cell.setCellStyle(style);
            }

            int rowNum = 1;
            for (Attendance attendance : attendanceList) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(attendance.getAttendanceId() != null ? attendance.getAttendanceId() : 0);
                row.createCell(1).setCellValue(attendance.getCourseId());
                row.createCell(2).setCellValue(attendance.getUserId());
                row.createCell(3).setCellValue(attendance.getAttendanceDate() != null ? attendance.getAttendanceDate().format(DATE_FORMATTER) : "");
                row.createCell(4).setCellValue(formatStatus(attendance.getStatus()));
                row.createCell(5).setCellValue(attendance.getSeatLocation() != null ? attendance.getSeatLocation() : "");
                row.createCell(6).setCellValue(attendance.getCheckinTime() != null ? attendance.getCheckinTime().format(DATETIME_FORMATTER) : "");
                row.createCell(7).setCellValue(attendance.getCheckoutTime() != null ? attendance.getCheckoutTime().format(DATETIME_FORMATTER) : "");
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private static String formatStatus(String status) {
        if (status == null) return "";
        return switch (status) {
            case "present" -> "出勤";
            case "absent" -> "缺勤";
            case "late" -> "迟到";
            case "early" -> "早退";
            case "leave" -> "请假";
            default -> status;
        };
    }
}
