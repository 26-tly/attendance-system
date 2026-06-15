package com.example.attendance.jpa.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendance_history")
public class AttendanceHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "course_id", nullable = false)
    private Integer courseId;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    @Column(name = "session_code", length = 64)
    private String sessionCode;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "checkin_time")
    private LocalDateTime checkinTime;

    @Column(name = "is_makes_up", nullable = false)
    private Boolean isMakesUp = false;

    @Column(name = "makes_up_reason")
    private String makesUpReason;

    @Column(name = "makes_up_time")
    private LocalDateTime makesUpTime;

    @Column(name = "makes_up_operator")
    private Integer makesUpOperator;

    public AttendanceHistory() {}

    public AttendanceHistory(Integer courseId, Long studentId, Integer userId, 
                            LocalDate attendanceDate, String sessionCode, String status) {
        this.courseId = courseId;
        this.studentId = studentId;
        this.userId = userId;
        this.attendanceDate = attendanceDate;
        this.sessionCode = sessionCode;
        this.status = status;
        this.checkinTime = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public LocalDate getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(LocalDate attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public String getSessionCode() {
        return sessionCode;
    }

    public void setSessionCode(String sessionCode) {
        this.sessionCode = sessionCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCheckinTime() {
        return checkinTime;
    }

    public void setCheckinTime(LocalDateTime checkinTime) {
        this.checkinTime = checkinTime;
    }

    public Boolean getIsMakesUp() {
        return isMakesUp;
    }

    public void setIsMakesUp(Boolean isMakesUp) {
        this.isMakesUp = isMakesUp;
    }

    public String getMakesUpReason() {
        return makesUpReason;
    }

    public void setMakesUpReason(String makesUpReason) {
        this.makesUpReason = makesUpReason;
    }

    public LocalDateTime getMakesUpTime() {
        return makesUpTime;
    }

    public void setMakesUpTime(LocalDateTime makesUpTime) {
        this.makesUpTime = makesUpTime;
    }

    public Integer getMakesUpOperator() {
        return makesUpOperator;
    }

    public void setMakesUpOperator(Integer makesUpOperator) {
        this.makesUpOperator = makesUpOperator;
    }
}
