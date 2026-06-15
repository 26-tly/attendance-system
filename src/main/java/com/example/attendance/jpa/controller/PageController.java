package com.example.attendance.jpa.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/index")
    public String index() {
        return "index";
    }

    @GetMapping("/student_index")
    public String studentIndex() {
        return "student_index";
    }

    @GetMapping("/teacher_index")
    public String teacherIndex() {
        return "teacher_index";
    }

    @GetMapping("/teacher_checkin")
    public String teacherCheckin() {
        return "teacher_checkin";
    }

    @GetMapping("/student_qrcode")
    public String studentQrcode() {
        return "student_qrcode";
    }

    @GetMapping("/student_checkin")
    public String studentCheckin() {
        return "student_checkin";
    }

    @GetMapping("/student_leave_apply")
    public String studentLeaveApply() {
        return "student_leave_apply";
    }

    @GetMapping("/student_attendance")
    public String studentAttendance() {
        return "student_attendance";
    }

    @GetMapping("/student_leave")
    public String studentLeave() {
        return "student_leave";
    }

    @GetMapping("/studentForm")
    public String studentForm(){
        return "studentForm";
    }

    @GetMapping("/studentList")
    public String studentList(){
        return "studentList";
    }

    @GetMapping("/student_import")
    public String studentImport(){
        return "student_import";
    }

    @GetMapping("/attendance/checkin")
    public String checkin(){
        return "attendance/checkin";
    }

    @GetMapping("/attendance/list")
    public String attendanceList(){
        return "attendance/list";
    }

    @GetMapping("/course/list")
    public String courseList(){
        return "course_list";
    }

    @GetMapping("/course/form")
    public String courseForm(){
        return "course/form";
    }

    @GetMapping("/leave/apply")
    public String leaveApply(){
        return "leave/apply";
    }

    @GetMapping("/leave/list")
    public String leaveList(){
        return "leave/list";
    }

    @GetMapping("/logs")
    public String logs(){
        return "logs";
    }

    @GetMapping("/test")
    public String test(){
        return "test";
    }

    @GetMapping("/course-student/manage")
    public String courseStudentManage(){
        return "course_student_manage";
    }

    @GetMapping("/attendance/history")
    public String attendanceHistory(){
        return "attendance_history";
    }

    @GetMapping("/attendance/logs")
    public String attendanceLogs(){
        return "attendance_logs";
    }
}
