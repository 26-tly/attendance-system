-- 创建数据库（兼容所有SQL Server版本）


IF DB_ID('attendance_system') IS NULL
CREATE DATABASE attendance_system;
GO

USE attendance_system;
GO

-- 用户表
IF NOT EXISTS (SELECT * FROM sys.objects WHERE type='U' AND name=N'[user]')
CREATE TABLE [user] (
                        user_id INT IDENTITY(1,1) PRIMARY KEY,
                        username NVARCHAR(50) NOT NULL UNIQUE,
                        password NVARCHAR(100) NOT NULL,
                        user_role NVARCHAR(20) NOT NULL CHECK (user_role IN ('admin', 'teacher')),
                        create_time DATETIME DEFAULT GETDATE()
);
GO

-- 课程表
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name='course')
CREATE TABLE course (
                        course_id INT IDENTITY(1,1) PRIMARY KEY,
                        course_name NVARCHAR(100) NOT NULL,
                        classroom NVARCHAR(50) NOT NULL,
                        course_desc NVARCHAR(500),
                        create_time DATETIME DEFAULT GETDATE()
);
GO

-- 选课表
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name='course_selection')
CREATE TABLE course_selection (
                                  selection_id INT IDENTITY(1,1) PRIMARY KEY,
                                  user_id INT NOT NULL,
                                  course_id INT NOT NULL,
                                  selection_time DATETIME DEFAULT GETDATE()
);
GO

-- 考勤表
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name='attendance')
CREATE TABLE attendance (
                            attendance_id INT IDENTITY(1,1) PRIMARY KEY,
                            course_id INT NOT NULL,
                            user_id INT NOT NULL,
                            attendance_date DATE NOT NULL,
                            status NVARCHAR(20) NOT NULL CHECK (status IN ('present', 'absent', 'leave')),
                            seat_location NVARCHAR(20)
);
GO

-- 插入测试数据（仅当表为空时插入）
IF NOT EXISTS (SELECT * FROM [user])
    INSERT INTO [user] (username, password, user_role)
    VALUES
        ('admin','admin123','admin'),
        ('teacher_zhang','teacher123','teacher');
GO

IF NOT EXISTS (SELECT * FROM course)
    INSERT INTO course (course_name, classroom, course_desc)
    VALUES
        ('数据库系统原理','302教室(30座单排)','核心专业课'),
        ('Java程序设计','405教室(双排)','面向对象编程');
GO

IF NOT EXISTS (SELECT * FROM course_selection)
    INSERT INTO course_selection (user_id, course_id)
    VALUES (1,1),(1,2),(2,1),(2,2),(1,1);
GO

IF NOT EXISTS (SELECT * FROM attendance)
    INSERT INTO attendance (course_id, user_id, attendance_date, status, seat_location)
    VALUES
        (1,1,'2026-04-01','present','第1排1号'),
        (1,2,'2026-04-01','present','第1排2号'),
        (2,1,'2026-04-01','absent','未签到'),
        (1,1,'2026-03-31','present','第2排3号'),
        (2,2,'2026-03-31','leave','请假');
GO

-- 查询所有数据
SELECT * FROM [user];
SELECT * FROM course;
SELECT * FROM course_selection;
SELECT * FROM attendance;
GO