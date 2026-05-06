package com.example.attendance.jpa.service;

import com.example.attendance.dto.LoginRequest;
import com.example.attendance.dto.RegisterRequest;
import com.example.attendance.jpa.entity.User;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Map;
public interface JpaUserService {


    int addUser(User user);

    User findByUserId(Integer userId);

    User findByUsername(String username);

    List<User> findByUserRole(String userRole);


    @PreAuthorize("jasRole('ADMIN')")
    int updateUser(User user);
    @PreAuthorize("hasRole('ADMIN')")
    int deleteByUserId(Integer userId);
    // 登录
    @PreAuthorize("permitAll()")
    Map<String,Object> login(LoginRequest request);
    //注册
    @PreAuthorize("permitAll()")
    Map<String,Object> register(RegisterRequest request);
}
