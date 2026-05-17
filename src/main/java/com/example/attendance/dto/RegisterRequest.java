package com.example.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor // 自动生成无参构造
@AllArgsConstructor // 自动生成全参构造
public class RegisterRequest {
    private String username;
    private String password;
    private String confirmPassword;
}
