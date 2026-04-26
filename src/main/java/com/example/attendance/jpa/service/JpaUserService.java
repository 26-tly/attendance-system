package com.example.attendance.jpa.service;

import com.example.attendance.jpa.entity.User;

import java.util.List;
public interface JpaUserService {

    int addUser(User user);

    User findByUserId(Integer userId);

    User findByUsername(String username);

    List<User> findByUserRole(String userRole);



    int updateUser(User user);

    int deleteByUserId(Integer userId);
}
