# 数据库设计文档

## 一、数据库概述

本系统使用 **SQL Server** 数据库，数据库名称为 `attendance_system`。

---

## 二、数据库连接配置

```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=attendance_system;encrypt=true;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=<your_password>
```

---

## 三、数据表详细设计

### 1. [user] 用户表

存储系统所有用户（管理员、教师、学生）。

| 字段名 | 类型 | 说明 | 备注 |
|--------|------|------|------|
| user_id | INT | 主键 | 自增 |
| username | NVARCHAR(50) | 用户名 | 唯一，非空（学生用学号） |
| password | NVARCHAR(100) | 密码 | BCrypt加密 |
| user_role | NVARCHAR(20) | 角色 | admin / teacher / student |
| create_time | DATETIME | 创建时间 | 默认 GETDATE() |

### 2. student 学生表

存储学生详细信息。

| 字段名 | 类型 | 说明 | 备注 |
|--------|------|------|------|
| id | BIGINT | 主键 | 自增 |
| student_no | NVARCHAR(50) | 学号 | 唯一，对应 [user].username |
| name | NVARCHAR(50) | 姓名 | 非空 |
| gender | NVARCHAR(10) | 性别 | 男 / 女 |
| birth_date | DATE | 出生日期 | - |
| phone | NVARCHAR(20) | 联系电话 | - |

### 3. course 课程表

存储课程信息。

| 字段名 | 类型 | 说明 | 备注 |
|--------|------|------|------|
| course_id | INT | 主键 | 自增 |
| course_name | NVARCHAR(100) | 课程名称 | 非空 |
| classroom | NVARCHAR(50) | 上课地点 | 非空 |
| course_desc | NVARCHAR(500) | 课程描述 | - |
| create_time | DATETIME | 创建时间 | 默认 GETDATE() |
| start_time | DATETIME | 上课时间 | 非空 |
| end_time | DATETIME | 下课时间 | 非空 |

### 4. course_student 课程-学生关联表

记录学生与课程的多对多关系。

| 字段名 | 类型 | 说明 | 备注 |
|--------|------|------|------|
| id | BIGINT | 主键 | 自增 |
| course_id | INT | 课程ID | 外键 → course.course_id |
| student_id | BIGINT | 学生ID | 外键 → student.id |
| enroll_time | DATETIME | 加入时间 | 默认 GETDATE() |

### 5. attendance 考勤记录表

记录每次签到信息（实时数据表）。

| 字段名 | 类型 | 说明 | 备注 |
|--------|------|------|------|
| attendance_id | INT | 主键 | 自增 |
| course_id | INT | 课程ID | 外键 → course.course_id |
| user_id | INT | 用户ID | 外键 → [user].user_id |
| attendance_date | DATE | 考勤日期 | 非空 |
| status | NVARCHAR(20) | 状态 | present / absent / leave / late |
| seat_location | NVARCHAR(20) | 座位号 | - |
| checkin_time | DATETIME | 签到时间 | - |
| checkout_time | DATETIME | 签退时间 | - |
| session_code | NVARCHAR(64) | 签到会话码 | 关联 checkin_session.session_code |

### 6. attendance_history 考勤历史表

记录考勤历史（用于补签、教师端历史查询，整合两表数据）。

| 字段名 | 类型 | 说明 | 备注 |
|--------|------|------|------|
| id | BIGINT | 主键 | 自增 |
| course_id | INT | 课程ID | 外键 → course.course_id |
| student_id | BIGINT | 学生ID | 外键 → student.id |
| user_id | INT | 用户ID | 外键 → [user].user_id |
| attendance_date | DATE | 考勤日期 | 非空 |
| session_code | NVARCHAR(64) | 签到会话码 | - |
| status | NVARCHAR(20) | 状态 | present / absent / leave / late |
| checkin_time | DATETIME | 签到时间 | - |
| is_makes_up | BIT | 是否补签 | 0 / 1 |
| makes_up_reason | NVARCHAR(500) | 补签原因 | - |
| makes_up_time | DATETIME | 补签时间 | - |
| makes_up_operator | INT | 补签操作人ID | 外键 → [user].user_id |

### 7. checkin_session 签到会话表

教师发起的签到会话。

| 字段名 | 类型 | 说明 | 备注 |
|--------|------|------|------|
| session_id | INT | 主键 | 自增 |
| teacher_id | INT | 教师ID | 外键 → [user].user_id |
| course_id | INT | 课程ID | 外键 → course.course_id |
| session_code | NVARCHAR(64) | 会话码 | 唯一，用于二维码 |
| start_time | DATETIME | 开始时间 | 非空 |
| end_time | DATETIME | 结束时间 | 非空 |
| status | NVARCHAR(20) | 状态 | active / expired / closed |
| created_time | DATETIME | 创建时间 | 默认 GETDATE() |
| total_students | INT | 应到人数 | - |
| checked_students | INT | 已签到人数 | 缓存值（实际以 attendance 表为准） |

### 8. leave_application 请假申请表

学生请假申请记录。

| 字段名 | 类型 | 说明 | 备注 |
|--------|------|------|------|
| leave_id | INT | 主键 | 自增 |
| user_id | INT | 用户ID | 外键 → [user].user_id |
| course_id | INT | 课程ID | 外键 → course.course_id |
| leave_type | NVARCHAR(20) | 请假类型 | sick / personal / business |
| start_date | DATE | 开始日期 | - |
| end_date | DATE | 结束日期 | - |
| reason | NVARCHAR(500) | 请假原因 | - |
| status | NVARCHAR(20) | 状态 | pending / approved / rejected |
| apply_time | DATETIME | 申请时间 | 默认 GETDATE() |
| approve_time | DATETIME | 审批时间 | - |
| approve_comment | NVARCHAR(500) | 审批意见 | - |
| approver_id | INT | 审批人ID | 外键 → [user].user_id |

### 9. operation_log 操作日志表

系统操作记录。

| 字段名 | 类型 | 说明 | 备注 |
|--------|------|------|------|
| log_id | BIGINT | 主键 | 自增 |
| user_id | INT | 用户ID | 外键 → [user].user_id |
| username | NVARCHAR(50) | 用户名 | - |
| operation_type | NVARCHAR(50) | 操作类型 | - |
| operation_desc | NVARCHAR(500) | 操作描述 | - |
| ip_address | VARCHAR(50) | IP地址 | - |
| create_time | DATETIME | 创建时间 | 默认 GETDATE() |

### 10. sys_role 角色表（RBAC）

| 字段名 | 类型 | 说明 | 备注 |
|--------|------|------|------|
| id | BIGINT | 主键 | 自增 |
| role_code | NVARCHAR(50) | 角色编码 | 唯一 |
| role_name | NVARCHAR(100) | 角色名称 | - |
| description | NVARCHAR(200) | 描述 | - |
| status | INT | 状态 | 1=启用，0=禁用 |

### 11. sys_permission 权限表（RBAC）

| 字段名 | 类型 | 说明 | 备注 |
|--------|------|------|------|
| id | BIGINT | 主键 | 自增 |
| perm_code | NVARCHAR(100) | 权限编码 | 唯一 |
| perm_name | NVARCHAR(100) | 权限名称 | - |
| description | NVARCHAR(200) | 描述 | - |
| status | INT | 状态 | 1=启用，0=禁用 |

### 12. sys_role_permission 角色-权限关联表

| 字段名 | 类型 | 说明 | 备注 |
|--------|------|------|------|
| id | BIGINT | 主键 | 自增 |
| role_id | BIGINT | 角色ID | 外键 → sys_role.id |
| perm_id | BIGINT | 权限ID | 外键 → sys_permission.id |

### 13. attendance_log 考勤日志表

记录考勤相关操作的详细日志。

| 字段名 | 类型 | 说明 | 备注 |
|--------|------|------|------|
| id | BIGINT | 主键 | 自增 |
| user_id | INT | 用户ID | - |
| action | NVARCHAR(50) | 操作 | checkin/checkout/makes_up |
| attendance_id | INT | 考勤记录ID | - |
| detail | NVARCHAR(500) | 详情 | - |
| create_time | DATETIME | 创建时间 | 默认 GETDATE() |

---

## 四、数据字典

### 4.1 用户角色 (user_role)
| 值 | 说明 |
|----|------|
| admin | 管理员 |
| teacher | 教师 |
| student | 学生 |

### 4.2 考勤状态 (status)
| 值 | 说明 |
|----|------|
| present | 正常签到 |
| late | 迟到 |
| early | 早退 |
| absent | 缺勤 |
| leave | 请假 |

### 4.3 请假类型 (leave_type)
| 值 | 说明 |
|----|------|
| sick | 病假 |
| personal | 事假 |
| business | 公假 |

### 4.4 请假状态 (status)
| 值 | 说明 |
|----|------|
| pending | 待审批 |
| approved | 已批准 |
| rejected | 已拒绝 |

### 4.5 签到会话状态 (status)
| 值 | 说明 |
|----|------|
| active | 进行中 |
| expired | 已过期 |
| closed | 已结束 |

### 4.6 补签标识 (is_makes_up)
| 值 | 说明 |
|----|------|
| 0 | 正常签到 |
| 1 | 补签 |

---

## 五、表关系图（ER 概览）

```
[user] 1 ─┬─ n [attendance] n ─ 1 [course]
           │                          │
           │                          │
           ├─ n [leave_application] n ─┤
           │                          │
           ├─ n [operation_log]       │
           │                          │
           └─ 1 [user_role] ─ n [sys_role_permission] n ─ 1 [sys_permission]

[user] 1 ─ 1 [student] 1 ─ n [course_student] n ─ 1 [course]
[user] 1 ─ n [checkin_session] n ─ 1 [course]
[user] 1 ─ n [attendance_history] n ─ 1 [course]
```

---

## 六、初始化数据（SQL 脚本）

系统初始化 SQL 脚本位于 `sql/attendance_system_init.sql`，包含：
- 数据库创建（如果不存在）
- 所有表结构创建
- 默认管理员/教师账号（`admin/admin123`、`teacher_zhang/teacher123`）
- 示例课程、学生、考勤数据

执行方式：
```bash
# 在 SQL Server Management Studio 中打开并执行
sql/attendance_system_init.sql
```

---

## 七、常见问题

### 7.1 外键约束冲突
- `fk_att_user`：attendance.user_id 必须存在于 [user].user_id
- 解决方案：使用学生学号（4241xxxx）作为 user 表 username，并先在 user 表创建学生账号

### 7.2 统计人数不准确
- `checkin_session.checked_students` 是缓存值，**实际已签到人数以 `attendance` 表的实时统计为准**
- 系统已改造 `getSessionStats` / `getAllActiveSessions` 实时从 `attendance` 表统计
