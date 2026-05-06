package com.example.attendance.dto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
/*
登录请求DTO
专门接收前端账号密码参数
 */
@Data
@NoArgsConstructor // 自动生成无参构造
@AllArgsConstructor // 自动生成全参构造
public class LoginRequest {

    private String username;
    private String password;
}
