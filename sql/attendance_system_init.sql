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
                        user_role NVARCHAR(20) NOT NULL CHECK (user_role IN ('admin', 'teacher', 'student')),
                        create_time DATETIME DEFAULT GETDATE()
);
GO

-- 学生表
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name='student')
CREATE TABLE student (
                        id BIGINT IDENTITY(1,1) PRIMARY KEY,
                        student_no NVARCHAR(50) NOT NULL UNIQUE,
                        name NVARCHAR(50) NOT NULL,
                        gender NVARCHAR(10),
                        birth_date DATE,
                        phone NVARCHAR(20)
);
GO

-- 课程表
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name='course')
CREATE TABLE course (
                        course_id INT IDENTITY(1,1) PRIMARY KEY,
                        course_name NVARCHAR(100) NOT NULL,
                        classroom NVARCHAR(50) NOT NULL,
                        course_desc NVARCHAR(500),
                        create_time DATETIME DEFAULT GETDATE(),
                        start_time DATETIME NOT NULL,
                        end_time DATETIME NOT NULL
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
    INSERT INTO course (course_name, classroom, course_desc, start_time, end_time)
    VALUES
        ('数据库系统原理','302教室(30座单排)','核心专业课', '2026-04-01 08:00:00', '2026-04-01 09:30:00'),
        ('Java程序设计','405教室(双排)','面向对象编程', '2026-04-01 14:00:00', '2026-04-01 15:30:00');
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

IF NOT EXISTS (SELECT * FROM student)
    INSERT INTO student (student_no, name, gender, birth_date, phone)
    VALUES
        ('42411120', '田龙羽', '男', '2004-05-08', '13800138001'),
        ('42411026', '敬凌杰', '男', '2004-03-15', '13800138002'),
        ('42411027', '张小明', '男', '2004-06-20', '13800138003'),
        ('42411028', '李小红', '女', '2004-08-10', '13800138004');
GO

-- 请假申请表
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name='leave_application')
CREATE TABLE leave_application (
    leave_id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT NOT NULL,
    course_id INT NOT NULL,
    leave_type NVARCHAR(20) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    reason NVARCHAR(500),
    status NVARCHAR(20) NOT NULL DEFAULT 'pending',
    apply_time DATETIME DEFAULT GETDATE(),
    approve_time DATETIME,
    approve_comment NVARCHAR(500),
    approver_id INT
);
GO

IF NOT EXISTS (SELECT * FROM leave_application)
    INSERT INTO leave_application (user_id, course_id, leave_type, start_date, end_date, reason, status)
    VALUES
        (1, 1, 'sick', '2026-04-05', '2026-04-06', '感冒发烧', 'approved'),
        (2, 1, 'personal', '2026-04-03', '2026-04-03', '家中有事', 'pending');
GO

-- 查询所有数据
SELECT * FROM [user];
SELECT * FROM course;
SELECT * FROM course_selection;
SELECT * FROM attendance;
SELECT * FROM student;
GO