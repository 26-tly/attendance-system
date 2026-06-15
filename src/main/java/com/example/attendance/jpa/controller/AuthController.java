package com.example.attendance.jpa.controller;

import com.example.attendance.common.Result;
import com.example.attendance.jpa.entity.User;
import com.example.attendance.jpa.repository.UserRepository;
import com.example.attendance.jpa.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<Result<Map<String, Object>>> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        if (username == null || password == null) {
            return ResponseEntity.ok(Result.error("用户名和密码不能为空"));
        }

        User user = userRepository.findByUsername(username);
        if (user == null) {
            logger.warn("登录失败：用户不存在 - {}", username);
            return ResponseEntity.ok(Result.error("用户名或密码错误"));
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            logger.warn("登录失败：密码错误 - {}", username);
            return ResponseEntity.ok(Result.error("用户名或密码错误"));
        }

        String token = jwtUtil.generateToken(
                user.getUserId().longValue(),
                user.getUsername(),
                user.getUserRole()
        );

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userId", user.getUserId());
        data.put("username", user.getUsername());
        data.put("role", user.getUserRole());

        logger.info("用户登录成功 - {}", username);
        return ResponseEntity.ok(Result.success(data));
    }

    @PostMapping("/register")
    public ResponseEntity<Result<User>> register(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");
        String role = request.get("role");

        if (username == null || password == null) {
            return ResponseEntity.ok(Result.error("用户名和密码不能为空"));
        }

        if (userRepository.findByUsername(username) != null) {
            return ResponseEntity.ok(Result.error("用户名已存在"));
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setUserRole(role != null ? role : "student");

        userRepository.save(user);
        logger.info("用户注册成功 - {}", username);
        return ResponseEntity.ok(Result.success(user));
    }

    @GetMapping("/validate")
    public ResponseEntity<Result<Map<String, Object>>> validateToken(@RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.ok(Result.error("无效的token"));
        }

        String tokenStr = token.substring(7);
        if (!jwtUtil.validateToken(tokenStr)) {
            return ResponseEntity.ok(Result.error("token已过期或无效"));
        }

        Long userId = jwtUtil.getUserIdFromToken(tokenStr);
        String username = jwtUtil.getUsernameFromToken(tokenStr);
        String role = jwtUtil.getRoleFromToken(tokenStr);

        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("username", username);
        data.put("role", role);

        return ResponseEntity.ok(Result.success(data));
    }
}