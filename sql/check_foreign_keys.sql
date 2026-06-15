-- 检查数据库中的外键约束
-- 查询所有外键约束
USE attendance_system;
GO

-- 查看所有外键约束
SELECT
    fk.name AS 外键名称,
    OBJECT_NAME(fk.parent_object_id) AS 表名,
    COL_NAME(fc.parent_object_id, fc.parent_column_id) AS 列名,
    OBJECT_NAME(fc.referenced_object_id) AS 引用表名,
    COL_NAME(fc.referenced_object_id, fc.referenced_column_id) AS 引用列名
FROM sys.foreign_keys AS fk
INNER JOIN sys.foreign_key_columns AS fc
    ON fk.object_id = fc.constraint_object_id;

-- 删除可能存在的外键约束（根据查询结果选择性执行）
-- 示例：如果 FK_attendance_course 外键约束存在，可以这样删除
-- ALTER TABLE attendance DROP CONSTRAINT FK_attendance_course;
-- ALTER TABLE attendance DROP CONSTRAINT FK_attendance_user;

-- 重新启用外键约束（如果之前禁用了）
-- ALTER TABLE attendance WITH CHECK CHECK CONSTRAINT ALL;
