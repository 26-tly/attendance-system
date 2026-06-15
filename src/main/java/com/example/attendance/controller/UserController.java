package com.example.attendance.controller;
import com.example.attendance.entity.User;
import com.example.attendance.jpa.util.JwtUtil;
import com.example.attendance.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 1. 新增用户（仅管理员可创建教师用户）
     * POST /user/add
     * 请求体示例：{"username":"teacher01","password":"123456","userRole":"teacher"}
     */
    @PostMapping("/add")
    public Map<String, Object> addUser(@RequestBody User user, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        try {
            String currentRole = (String) request.getAttribute("role");
            
            // 验证权限：只有管理员可以创建教师用户
            String targetRole = user.getUserRole();
            if (targetRole == null || targetRole.isEmpty()) {
                targetRole = "student"; // 默认角色为学生
            }
            
            if ("teacher".equalsIgnoreCase(targetRole) || "admin".equalsIgnoreCase(targetRole)) {
                if (!"admin".equalsIgnoreCase(currentRole)) {
                    result.put("code", 403);
                    result.put("msg", "权限不足：只有管理员可以创建教师或管理员账户");
                    return result;
                }
            }
            
            user.setUserRole(targetRole);
            
            // 加密密码
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            int count = userService.addUser(user);
            if (count > 0) {
                result.put("code", 200);
                result.put("msg", "注册成功");
                result.put("data", user);
            } else {
                result.put("code", 500);
                result.put("msg", "注册失败");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", "注册失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 2. 根据ID查询用户
     * GET /user/{userId}
     * 示例：GET /user/1
     */
    @GetMapping("/{userId}")
    public Map<String, Object> findById(@PathVariable Integer userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            User user = userService.findById(userId);
            if (user != null) {
                result.put("code", 200);
                result.put("msg", "查询成功");
                result.put("data", user);
            } else {
                result.put("code", 404);
                result.put("msg", "用户不存在");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", "查询失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 3. 根据用户名查询（登录验证）
     * POST /user/login
     * 请求体示例：{"username":"admin","password":"123456"}
     */
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        try {
            User dbUser = userService.findByUsername(user.getUsername());
            if (dbUser != null) {
                boolean passwordMatch = passwordEncoder.matches(user.getPassword(), dbUser.getPassword());
                
                // 向后兼容：如果BCrypt验证失败，且数据库中的密码不是BCrypt格式（不以$2开头），尝试明文匹配
                if (!passwordMatch && dbUser.getPassword() != null && !dbUser.getPassword().startsWith("$2")) {
                    passwordMatch = user.getPassword().equals(dbUser.getPassword());
                }
                
                if (passwordMatch) {
                    String token = jwtUtil.generateToken(
                            dbUser.getUserId().longValue(),
                            dbUser.getUsername(),
                            dbUser.getUserRole()
                    );
                    
                    Map<String, Object> data = new HashMap<>();
                    data.put("token", token);
                    data.put("userId", dbUser.getUserId());
                    data.put("username", dbUser.getUsername());
                    data.put("userRole", dbUser.getUserRole());
                    
                    result.put("code", 200);
                    result.put("msg", "登录成功");
                    result.put("data", data);
                } else {
                    result.put("code", 401);
                    result.put("msg", "用户名或密码错误");
                }
            } else {
                result.put("code", 401);
                result.put("msg", "用户名或密码错误");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", "登录失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 4. 查询所有教师用户
     * GET /user/teachers
     */
    @GetMapping("/teachers")
    public Map<String, Object> findAllTeachers() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<User> teacherList = userService.findAllTeachers();
            result.put("code", 200);
            result.put("msg", "查询成功");
            result.put("data", teacherList);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", "查询失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 5. 更新用户信息
     * PUT /user/update
     * 请求体示例：{"userId":1,"username":"teacher01","password":"new123","userRole":"teacher"}
     */
    @PutMapping("/update")
    public Map<String, Object> updateUser(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        try {
            int count = userService.updateUser(user);
            if (count > 0) {
                result.put("code", 200);
                result.put("msg", "更新成功");
            } else {
                result.put("code", 404);
                result.put("msg", "用户不存在，更新失败");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", "更新失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 6. 根据ID删除用户
     * DELETE /user/delete/{userId}
     * 示例：DELETE /user/delete/1
     */
    @DeleteMapping("/delete/{userId}")
    public Map<String, Object> deleteUser(@PathVariable Integer userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            int count = userService.deleteById(userId);
            if (count > 0) {
                result.put("code", 200);
                result.put("msg", "删除成功");
            } else {
                result.put("code", 404);
                result.put("msg", "用户不存在，删除失败");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", "删除失败：" + e.getMessage());
        }
        return result;
    }
}
