package com.example.attendance.jpa.service;

import com.example.attendance.jpa.entity.User;

import java.util.List;
public interface JpaUserService {

    int addUser(User user);

    User findById(Integer userId);

    User findByUsername(String username);

    List<User> findAllTeachers();

    int updateUser(User user);

    int deleteById(Integer userId);
}
