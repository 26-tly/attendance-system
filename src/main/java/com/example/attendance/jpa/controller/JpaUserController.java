package com.example.attendance.jpa.controller;

import com.example.attendance.dto.RegisterRequest;
import com.example.attendance.jpa.entity.User;
import com.example.attendance.jpa.service.JpaUserService;
import com.example.attendance.dto.LoginRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
/**
 * JPA版用户控制器，和老JDBC接口功能完全对齐
 */
@RestController
@RequestMapping("/jpa/user") // 接口前缀，和老接口区分开
public class JpaUserController {
    // 注入JPA的Service
    private JpaUserService jpaUserService;

    /**
     * 新增用户
     * POST 请求：http://localhost:8080/jpa/user/add
     */
    @PostMapping("/add")
    public String addUser(@RequestBody User user) {
        int rows = jpaUserService.addUser(user);
        return rows > 0 ? "新增成功" : "新增失败";
    }

    /**
     * 根据ID查询用户
     * GET 请求：http://localhost:8080/jpa/user/find/1
     */
    @GetMapping("/find/{userId}")
    public User findByUserId(@PathVariable Integer userId) {
        return jpaUserService.findByUserId(userId);
    }

    /**
     * 根据用户名查询用户
     * GET 请求：http://localhost:8080/jpa/user/find/username?username=xxx
     */
    @GetMapping("/find/username")
    public User findByUsername(@RequestParam String username) {
        return jpaUserService.findByUsername(username);
    }

    /**
     * 查询所有教师用户
     * GET 请求：http://localhost:8080/jpa/user/teachers
     */
    @GetMapping("/teachers")
    public List<User> findUserRole(@RequestParam String userRole) {
        return jpaUserService.findByUserRole(userRole);
    }

    /**
     * 更新用户信息
     * PUT 请求：http://localhost:8080/jpa/user/update
     */
    @PutMapping("/update")
    public String updateUser(@RequestBody User user) {
        int rows = jpaUserService.updateUser(user);
        return rows > 0 ? "更新成功" : "更新失败";
    }

    /**
     * 根据ID删除用户
     * DELETE 请求：http://localhost:8080/jpa/user/delete/1
     */
    @DeleteMapping("/delete/{userId}")
    public String deleteById(@PathVariable Integer userId) {
        int rows = jpaUserService.deleteByUserId(userId);
        return rows > 0 ? "删除成功" : "删除失败";
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest){
        return ResponseEntity.ok(jpaUserService.login(loginRequest));
    }

    @GetMapping("/toLogin/")
    public String toLogin(){
        return "login";
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest){
        return ResponseEntity.ok(jpaUserService.register(registerRequest));
    }
    @GetMapping("/toRegister/")
    public String toRegister(){
        return "register";
    }
    @GetMapping("/index/")
    public String toIndex(){
        return "index";
    }
}
