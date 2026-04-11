package com.example.attendance.service;
import com.example.attendance.entity.User;
import java.util.List;
public interface UserService {
    //新增教师用户
    int addUser(User user);
    //根据id查询用户
    User findById(Integer userId);
    //根据用户名查询用户
    User findByUsername(String username);
    //查询所有教师用户
    List<User> findAllTeachers();
    //更新用户信息
    int updateUser(User user);
    //根据id删除用户
    int deleteById(Integer userId);
}
