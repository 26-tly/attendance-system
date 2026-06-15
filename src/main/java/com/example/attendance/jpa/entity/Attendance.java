package com.example.attendance.jpa.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendance")
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendance_id")
    private Integer attendanceId;

    @Column(name = "course_id", nullable = false)
    private Integer courseId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "seat_location", length = 20)
    private String seatLocation;

    @Column(name = "checkin_time")
    private LocalDateTime checkinTime;

    @Column(name = "checkout_time")
    private LocalDateTime checkoutTime;

    @Column(name = "session_code", length = 64)
    private String sessionCode;

    public Attendance() {}

    public Attendance(Integer attendanceId, Integer courseId, Integer userId, LocalDate attendanceDate, 
                      String status, String seatLocation, LocalDateTime checkinTime, LocalDateTime checkoutTime) {
        this.attendanceId = attendanceId;
        this.courseId = courseId;
        this.userId = userId;
        this.attendanceDate = attendanceDate;
        this.status = status;
        this.seatLocation = seatLocation;
        this.checkinTime = checkinTime;
        this.checkoutTime = checkoutTime;
    }

    public Integer getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(Integer attendanceId) {
        this.attendanceId = attendanceId;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSeatLocation() {
        return seatLocation;
    }

    public void setSeatLocation(String seatLocation) {
        this.seatLocation = seatLocation;
    }

    public LocalDateTime getCheckinTime() {
        return checkinTime;
    }

    public void setCheckinTime(LocalDateTime checkinTime) {
        this.checkinTime = checkinTime;
    }

    public LocalDateTime getCheckoutTime() {
        return checkoutTime;
    }

    public void setCheckoutTime(LocalDateTime checkoutTime) {
        this.checkoutTime = checkoutTime;
    }

    public String getSessionCode() {
        return sessionCode;
    }

    public void setSessionCode(String sessionCode) {
        this.sessionCode = sessionCode;
    }

    public static AttendanceBuilder builder() {
        return new AttendanceBuilder();
    }

    public static class AttendanceBuilder {
        private Integer attendanceId;
        private Integer courseId;
        private Integer userId;
        private LocalDate attendanceDate;
        private String status;
        private String seatLocation;
        private LocalDateTime checkinTime;
        private LocalDateTime checkoutTime;

        public AttendanceBuilder attendanceId(Integer attendanceId) {
            this.attendanceId = attendanceId;
            return this;
        }

        public AttendanceBuilder courseId(Integer courseId) {
            this.courseId = courseId;
            return this;
        }

        public AttendanceBuilder userId(Integer userId) {
            this.userId = userId;
            return this;
        }

        public AttendanceBuilder attendanceDate(LocalDate attendanceDate) {
            this.attendanceDate = attendanceDate;
            return this;
        }

        public AttendanceBuilder status(String status) {
            this.status = status;
            return this;
        }

        public AttendanceBuilder seatLocation(String seatLocation) {
            this.seatLocation = seatLocation;
            return this;
        }

        public AttendanceBuilder checkinTime(LocalDateTime checkinTime) {
            this.checkinTime = checkinTime;
            return this;
        }

        public AttendanceBuilder checkoutTime(LocalDateTime checkoutTime) {
            this.checkoutTime = checkoutTime;
            return this;
        }

        public Attendance build() {
            return new Attendance(attendanceId, courseId, userId, attendanceDate, 
                                 status, seatLocation, checkinTime, checkoutTime);
        }
    }
}