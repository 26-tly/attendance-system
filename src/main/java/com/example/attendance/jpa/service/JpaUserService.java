package com.example.attendance.jpa.service;

import com.example.attendance.dto.LoginRequest;
import com.example.attendance.dto.RegisterRequest;
import com.example.attendance.jpa.entity.User;

import java.util.List;
import java.util.Map;
public interface JpaUserService {


    int addUser(User user);

    User findByUserId(Integer userId);

    User findByUsername(String username);

    List<User> findByUserRole(String userRole);


    int updateUser(User user);

    int deleteByUserId(Integer userId);
    
    Map<String,Object> login(LoginRequest request);
    
    Map<String,Object> register(RegisterRequest request);
}
