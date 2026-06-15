package com.example.attendance.jpa.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "course")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer courseId;

    @Column(name = "course_name", nullable = false)
    private String courseName;

    @Column(name = "classroom", nullable = false)
    private String classroom;

    @Column(name = "course_desc")
    private String courseDesc;

    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    public Course() {}

    public Course(Integer courseId, String courseName, String classroom, String courseDesc, 
                  LocalDateTime createTime, LocalDateTime startTime, LocalDateTime endTime) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.classroom = classroom;
        this.courseDesc = courseDesc;
        this.createTime = createTime;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @PrePersist
    void prePersist() {
        this.createTime = LocalDateTime.now();
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public String getCourseDesc() {
        return courseDesc;
    }

    public void setCourseDesc(String courseDesc) {
        this.courseDesc = courseDesc;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public static CourseBuilder builder() {
        return new CourseBuilder();
    }

    public static class CourseBuilder {
        private Integer courseId;
        private String courseName;
        private String classroom;
        private String courseDesc;
        private LocalDateTime createTime;
        private LocalDateTime startTime;
        private LocalDateTime endTime;

        public CourseBuilder courseId(Integer courseId) {
            this.courseId = courseId;
            return this;
        }

        public CourseBuilder courseName(String courseName) {
            this.courseName = courseName;
            return this;
        }

        public CourseBuilder classroom(String classroom) {
            this.classroom = classroom;
            return this;
        }

        public CourseBuilder courseDesc(String courseDesc) {
            this.courseDesc = courseDesc;
            return this;
        }

        public CourseBuilder createTime(LocalDateTime createTime) {
            this.createTime = createTime;
            return this;
        }

        public CourseBuilder startTime(LocalDateTime startTime) {
            this.startTime = startTime;
            return this;
        }

        public CourseBuilder endTime(LocalDateTime endTime) {
            this.endTime = endTime;
            return this;
        }

        public Course build() {
            return new Course(courseId, courseName, classroom, courseDesc, createTime, startTime, endTime);
        }
    }
}