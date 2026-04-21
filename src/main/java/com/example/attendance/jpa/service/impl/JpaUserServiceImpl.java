package com.example.attendance.jpa.service.impl;
import com.example.attendance.jpa.entity.User;
import com.example.attendance.jpa.repository.UserRepository;
import com.example.attendance.jpa.service.JpaUserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JpaUserServiceImpl implements JpaUserService {

    private UserRepository userRepository;

    /**
     * 新增用户
     */
    @Override
    public int addUser(User user) {
        // JPA保存，成功返回1，和老JDBC返回int对齐
        userRepository.save(user);
        return 1;
    }

    /**
     * 根据id查询用户
     */
    @Override
    public User findById(Integer userId) {
        Optional<User> optional = userRepository.findById(userId);
        // 找不到返回null，和老代码行为一致
        return optional.orElse(null);
    }

    /**
     * 根据用户名查询用户
     */
    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * 查询所有教师用户（假设user_role为teacher是教师）
     */
    @Override
    public List<User> findAllTeachers() {
        // 调用Repository自定义方法，查询角色为teacher的用户
        return userRepository.findAllTeachers();
    }

    /**
     * 更新用户信息
     */
    @Override
    public int updateUser(User user) {
        // JPA save: 有id就更新，无id就新增，这里只做更新
        userRepository.save(user);
        return 1;
    }

    /**
     * 根据id删除用户
     */
    @Override
    public int deleteById(Integer userId) {
        userRepository.deleteById(userId);
        return 1;
    }
}
