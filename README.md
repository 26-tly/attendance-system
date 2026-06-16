# 班级考勤管理系统

## 项目简介

班级考勤管理系统是一个基于 Spring Boot 4 开发的 Web 应用，面向高校/培训机构的日常考勤场景。系统提供**二维码签到**、课程管理、学生管理、考勤历史查询、请假审批、操作日志、RBAC 权限控制等核心功能，支持**教师端**与**学生端**双端访问。

## 技术栈

| 类别 | 技术 |
|------|------|
| 后端 | Spring Boot 4.0.5, Spring MVC, Spring Data JPA, Spring Security |
| 安全 | JWT (jjwt 0.12.5), BCrypt 密码加密 |
| 持久化 | SQL Server + Microsoft JDBC Driver 12.8 |
| 前端 | Thymeleaf, Bootstrap 5, Font Awesome, jQuery |
| 二维码 | QRCode.js (前端) |
| API 文档 | SpringDoc OpenAPI 2.3.0 (Swagger UI) |
| 工具 | Apache POI 5.2.5 (Excel 导入导出), Lombok 1.18.38 |
| 构建 | Maven 3.8+ |
| JDK | JDK 21+（已在 JDK 25 验证） |

## 功能特性

### 用户与权限
- 三级角色：**管理员 / 教师 / 学生**
- 基于 RBAC 的细粒度权限管理（`sys_role`、`sys_permission`、`sys_role_permission`）
- JWT Token 登录态，前端 LocalStorage 透传
- BCrypt 密码加密

### 课程与学生
- 课程信息增删改查、批量删除
- 学生信息管理、批量 Excel 导入、从学生池添加到课程
- 课程-学生多对多关联

### 考勤打卡（核心）
- **二维码签到**：教师生成签到码 → 学生微信扫一扫签到
- 传统签到/签退、迟到/早退自动判断
- 补签申请与审批（支持补签原因记录）

### 考勤历史查询
- 按用户、课程、日期、状态等条件多维筛选
- 分页查询 + 实时统计已签到人数
- **双表联合查询**：自动合并 `attendance` 和 `attendance_history` 数据
- **Excel 导出**（Apache POI）

### 请假管理
- 学生在线请假申请
- 教师/管理员审批流程

### 操作日志
- 全量操作审计
- 按用户/类型/时间筛选

## 快速开始

### 环境要求
- **JDK 21+**（推荐 JDK 25，已验证）
- Maven 3.8+
- SQL Server 2019+（默认端口 1433）
- 推荐：IntelliJ IDEA 2024+

### 安装步骤

1. **克隆项目**
   ```bash
   git clone <repository-url>
   cd attendance-system
   ```

2. **初始化数据库**
   - 打开 SQL Server Management Studio
   - 执行 `sql/attendance_system_init.sql`（自动创建数据库和表结构，插入测试数据）

3. **配置数据库连接**

   修改 `src/main/resources/application.properties`：
   ```properties
   spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=attendance_system;encrypt=true;trustServerCertificate=true
   spring.datasource.username=sa
   spring.datasource.password=your_password
   ```

4. **运行项目**

   ```bash
   # 开发模式
   ./mvnw spring-boot:run
   # 或 Windows
   mvnw.cmd spring-boot:run
   ```

5. **访问系统**
   - 主页：http://localhost/login
   - 默认端口 80（可在 `application.properties` 中修改）

### 默认账户

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | admin123 |
| 教师 | teacher_zhang | teacher123 |
| 教师 | teacher01 | 123456 |
| 教师 | teacher02 | 123456 |
| 学生 | 42411120 | 123456 |
| 学生 | 42411018 | 123456 |

> 注：学生的 username 即为学号；学生首次登录后可在「学生中心」修改密码。

## 关键配置说明

### application.properties
```properties
# 服务器（监听所有网卡，方便手机扫码访问）
server.port=80
server.address=0.0.0.0

# JWT
jwt.secret=attendance-system-secret-key-must-be-at-least-32-chars-for-security
jwt.expire-time=86400000  # 24小时

# 文件上传
spring.servlet.multipart.max-file-size=100MB
spring.servlet.multipart.max-request-size=100MB
file.upload.path=D:/upload/excel/
```

## 二维码签到使用说明

### 教师端流程
1. 登录系统 → 进入「二维码签到」页面
2. 选择课程，设置签到时间窗口
3. 点击「创建签到」→ 系统生成**签到会话码** + 二维码
4. 二维码 URL 格式：`http://<自动检测的本机IP>/student_qrcode?code=<会话码>`
5. 学生在规定时间内扫码签到
6. 教师端**每 5 秒自动刷新**已签到人数（实际以 `attendance` 表为准）
7. 点击「结束签到」可手动关闭会话

### 学生端流程
1. 微信扫一扫教师二维码
2. 若未登录，先登录系统（登录后自动跳回扫码页面提交签到）
3. 签到成功后会显示结果

### 网络要求
- 教师电脑与学生手机**必须在同一局域网**
- ⚠️ 校园网通常启用 **AP 隔离**（设备间不可互相访问），需切换到**手机热点**或**有线网络**

## 项目结构

```
attendance-system/
├── src/main/java/com/example/attendance/
│   ├── AttendanceSystemApplication.java    # Spring Boot 启动类
│   ├── common/                              # 通用类（Result 等）
│   ├── controller/                          # 控制层（原生）
│   ├── service/                             # 业务层（原生）
│   ├── entity/                              # 实体类（原生）
│   ├── dao/                                 # 数据访问（原生）
│   ├── dto/                                 # 数据传输对象
│   └── jpa/
│       ├── config/                          # 配置类（Security/WebMvc/文件上传）
│       ├── controller/                      # JPA 控制层
│       ├── service/impl/                    # 业务实现
│       ├── repository/                      # JPA 数据访问
│       ├── entity/                          # JPA 实体
│       ├── interceptor/                     # 鉴权拦截器
│       ├── init/                            # 数据初始化（角色/权限）
│       ├── exception/                       # 全局异常处理
│       └── util/                            # 工具类（Excel/JWT）
├── src/main/resources/
│   ├── templates/                           # Thymeleaf 模板
│   │   ├── login.html                       # 登录页
│   │   ├── teacher_checkin.html             # 教师二维码签到
│   │   ├── student_qrcode.html              # 学生扫码签到
│   │   ├── attendance/                      # 考勤模块
│   │   ├── course/                          # 课程模块
│   │   ├── leave/                           # 请假模块
│   │   └── ...
│   ├── static/                              # 静态资源
│   └── application.properties               # 配置文件
├── src/test/                                # 单元测试
├── sql/                                     # 数据库初始化脚本
│   ├── attendance_system_init.sql           # 主初始化脚本
│   ├── check_foreign_keys.sql
│   └── fix_foreign_keys.sql
├── pom.xml                                  # Maven 配置
├── README.md                                # 本文档
└── database.md                              # 数据库设计文档
```

## 核心 API 接口

### 认证
| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/login` | POST | 用户登录 |
| `/api/register` | POST | 用户注册 |

### 课程
| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/course` | POST/GET | 添加/列表 |
| `/api/course/{id}` | GET/PUT/DELETE | 详情/更新/删除 |

### 考勤
| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/attendance/checkin` | POST | 学生签到 |
| `/api/attendance/checkout` | POST | 学生签退 |
| `/api/attendance/history` | GET | 考勤历史查询（分页） |
| `/api/attendance/export` | GET | 导出 Excel |
| `/api/attendance/upload` | POST | Excel 批量导入 |

### 二维码签到（核心）
| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/checkin/session` | POST | 创建签到会话 |
| `/api/checkin/session/{id}/stats` | GET | 实时签到统计 |
| `/api/checkin/session/id/{id}` | GET | 获取会话详情（用于恢复二维码） |
| `/api/checkin/active` | GET | 获取所有进行中的签到 |
| `/api/checkin/validate` | POST | 学生提交签到 |
| `/api/checkin/session/{id}/close` | POST | 教师结束签到 |

### 辅助
| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/server/ip` | GET | 获取服务器局域网 IP（用于动态生成二维码） |

## 部署说明

### 打包
```bash
# 清理并打包（跳过测试）
./mvnw clean package -DskipTests

# 打包后的 JAR
target/attendance-system-0.0.1-SNAPSHOT.jar
```

### 运行
```bash
# 直接运行
java -jar target/attendance-system-0.0.1-SNAPSHOT.jar

# 指定端口
java -jar target/attendance-system-0.0.1-SNAPSHOT.jar --server.port=8081

# 后台运行（Linux）
nohup java -jar target/attendance-system-0.0.1-SNAPSHOT.jar > app.log 2>&1 &
```

### Windows 服务化（可选）
- 推荐使用 **nssm** 或 **WinSW** 将 JAR 注册为 Windows 服务

## 常见问题

### 1. 校园网扫码无法访问
- **原因**：校园网启用 AP 隔离
- **解决**：使用手机热点 / 有线网络

### 2. 二维码显示但签到人数不刷新
- **原因**：使用 `checkin_session.checked_students` 缓存值
- **状态**：✅ 已修复，`getSessionStats` 和 `getAllActiveSessions` 现在实时从 `attendance` 表统计

### 3. 学生签到后历史查询无数据
- **原因**：原来只查 `attendance_history` 表
- **状态**：✅ 已修复，`getAttendanceHistory` 现在合并两表数据并去重

### 4. 外键约束冲突（`fk_att_user`）
- **原因**：`attendance.user_id` 在 `[user]` 表中不存在
- **解决**：学生必须有对应 `[user]` 记录（username = 学号）

### 5. 登录失败
- 默认密码均为 123456 / admin123 / teacher123
- 密码使用 BCrypt 加密存储

## 联系方式

- **作者**：田龙羽
- **邮箱**：3292614546@qq.com

## 许可证

MIT License
