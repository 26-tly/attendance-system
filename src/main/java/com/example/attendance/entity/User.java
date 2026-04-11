package com.example.attendance.entity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 用户实体类（Lombok 简化版，完全对应数据库 user 表，不含 create_time）
 */
@Data // 自动生成 Getter/Setter、toString、equals、hashCode
@NoArgsConstructor // 自动生成无参构造
@AllArgsConstructor // 自动生成全参构造
public class User {
        private Integer userId;
        private String username;
        private String password;
        private String userRole;
}
