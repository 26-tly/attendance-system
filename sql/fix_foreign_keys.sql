-- 考勤系统外键约束处理脚本
-- 此脚本删除现有的外键约束并重新创建（允许级联删除）

USE attendance_system;
GO

-- 1. 首先删除所有相关表的外键约束
DECLARE @sql NVARCHAR(MAX) = N'';

SELECT @sql = @sql + N'
ALTER TABLE ' + OBJECT_NAME(fk.parent_object_id) + ' DROP CONSTRAINT ' + fk.name + ';
'
FROM sys.foreign_keys AS fk
WHERE OBJECT_NAME(fk.parent_object_id) IN ('attendance', 'course_selection', 'leave_application');

PRINT @sql;
EXEC sp_executesql @sql;

-- 2. 重新创建 attendance 表的外键约束（启用级联删除）
ALTER TABLE attendance
ADD CONSTRAINT FK_attendance_course
FOREIGN KEY (course_id) REFERENCES course(course_id)
ON DELETE CASCADE;

ALTER TABLE attendance
ADD CONSTRAINT FK_attendance_user
FOREIGN KEY (user_id) REFERENCES [user](user_id)
ON DELETE CASCADE;

-- 3. 重新创建 course_selection 表的外键约束（启用级联删除）
ALTER TABLE course_selection
ADD CONSTRAINT FK_course_selection_course
FOREIGN KEY (course_id) REFERENCES course(course_id)
ON DELETE CASCADE;

ALTER TABLE course_selection
ADD CONSTRAINT FK_course_selection_user
FOREIGN KEY (user_id) REFERENCES [user](user_id)
ON DELETE CASCADE;

-- 4. 重新创建 leave_application 表的外键约束（启用级联删除）
ALTER TABLE leave_application
ADD CONSTRAINT FK_leave_application_user
FOREIGN KEY (user_id) REFERENCES [user](user_id)
ON DELETE CASCADE;

ALTER TABLE leave_application
ADD CONSTRAINT FK_leave_application_course
FOREIGN KEY (course_id) REFERENCES course(course_id)
ON DELETE CASCADE;

PRINT '所有外键约束已更新，现在支持级联删除';
GO
