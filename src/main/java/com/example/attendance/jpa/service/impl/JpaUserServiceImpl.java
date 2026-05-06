package com.example.attendance.jpa.service.impl;
import com.example.attendance.jpa.entity.User;
import com.example.attendance.jpa.repository.UserRepository;
import com.example.attendance.jpa.service.JpaUserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import com.example.attendance.dto.LoginRequest;
import com.example.attendance.dto.RegisterRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.HashMap;
import java.util.Map;

import java.util.List;
import java.util.Optional;

@Service
public class JpaUserServiceImpl implements JpaUserService {

    private UserRepository userRepository;
    @Resource
    private PasswordEncoder passwordEncoder;


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
    public User findByUserId(Integer userId) {
        Optional<User> optional = userRepository.findByUserId(userId);
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
    public List<User> findByUserRole(String userRole) {
        // 调用Repository自定义方法，查询角色为teacher的用户
        return userRepository.findByUserRole(userRole);
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
    public int deleteByUserId(Integer userId) {
        userRepository.deleteByUserId(userId);
        return 1;
    }
    @Override
    public Map<String, Object> login(LoginRequest request) {
        // 查询用户
        User user = userRepository.findByUsername(request.getUsername());
                if(user == null){
                    throw new RuntimeException("用户名不存在");
                }

        // 校验密码
        if(!passwordEncoder.matches(request.getPassword(),user.getPassword())){
            throw new RuntimeException("密码错误");
        }

        Map<String,Object> map = new HashMap<>();
        map.put("msg","登录成功");
        map.put("username",user.getUsername());
        return map;
    }

    @Override
    public Map<String,Object> register(RegisterRequest request) {
        if(userRepository.existsByUsername(request.getUsername())){
            return Map.of("msg","账号已存在","code",400);
        }
        //密码加密
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        //保存用户信息
        userRepository.save(user);
        return Map.of("msg","注册成功","code",200);
    }
}
