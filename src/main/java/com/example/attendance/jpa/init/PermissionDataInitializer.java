package com.example.attendance.jpa.init;

import com.example.attendance.jpa.entity.Permission;
import com.example.attendance.jpa.entity.Role;
import com.example.attendance.jpa.entity.RolePermission;
import com.example.attendance.jpa.repository.PermissionRepository;
import com.example.attendance.jpa.repository.RolePermissionRepository;
import com.example.attendance.jpa.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class PermissionDataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(PermissionDataInitializer.class);

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private RolePermissionRepository rolePermissionRepository;

    @Override
    public void run(String... args) throws Exception {
        initRoles();
        initPermissions();
        initRolePermissions();
    }

    private void initRoles() {
        if (roleRepository.count() == 0) {
            Role adminRole = new Role();
            adminRole.setRoleCode("admin");
            adminRole.setRoleName("管理员");
            adminRole.setRoleDesc("系统管理员，拥有所有权限");
            adminRole.setSortOrder(1);
            adminRole.setStatus(1);
            roleRepository.save(adminRole);

            Role teacherRole = new Role();
            teacherRole.setRoleCode("teacher");
            teacherRole.setRoleName("教师");
            teacherRole.setRoleDesc("授课教师，可管理课程和学生");
            teacherRole.setSortOrder(2);
            teacherRole.setStatus(1);
            roleRepository.save(teacherRole);

            Role studentRole = new Role();
            studentRole.setRoleCode("student");
            studentRole.setRoleName("学生");
            studentRole.setRoleDesc("学生用户，可进行考勤打卡");
            studentRole.setSortOrder(3);
            studentRole.setStatus(1);
            roleRepository.save(studentRole);

            logger.info("初始化角色数据完成");
        }
    }

    private void initPermissions() {
        if (permissionRepository.count() == 0) {
            List<Permission> permissions = Arrays.asList(
                // 用户管理
                createPermission("user:view", "查看用户", "查看用户列表", "user", "GET", "/api/user/**"),
                createPermission("user:create", "创建用户", "创建新用户", "user", "POST", "/api/user/**"),
                createPermission("user:update", "修改用户", "修改用户信息", "user", "PUT", "/api/user/**"),
                createPermission("user:delete", "删除用户", "删除用户", "user", "DELETE", "/api/user/**"),
                
                // 课程管理
                createPermission("course:view", "查看课程", "查看课程列表", "course", "GET", "/api/course/**"),
                createPermission("course:create", "创建课程", "创建新课程", "course", "POST", "/api/course/**"),
                createPermission("course:update", "修改课程", "修改课程信息", "course", "PUT", "/api/course/**"),
                createPermission("course:delete", "删除课程", "删除课程", "course", "DELETE", "/api/course/**"),
                
                // 学生管理
                createPermission("student:view", "查看学生", "查看学生列表", "student", "GET", "/api/student/**"),
                createPermission("student:create", "创建学生", "创建新学生", "student", "POST", "/api/student/**"),
                createPermission("student:update", "修改学生", "修改学生信息", "student", "PUT", "/api/student/**"),
                createPermission("student:delete", "删除学生", "删除学生", "student", "DELETE", "/api/student/**"),
                
                // 考勤管理
                createPermission("attendance:view", "查看考勤", "查看考勤记录", "attendance", "GET", "/api/attendance/**"),
                createPermission("attendance:manage", "管理考勤", "管理考勤记录", "attendance", "*", "/api/attendance/**"),
                
                // 请假管理
                createPermission("leave:view", "查看请假", "查看请假申请", "leave", "GET", "/api/leave/**"),
                createPermission("leave:approve", "审批请假", "审批请假申请", "leave", "POST", "/api/leave/approve/**"),
                createPermission("leave:apply", "申请请假", "提交请假申请", "leave", "POST", "/api/leave/apply"),
                
                // 签到管理
                createPermission("checkin:create", "发起签到", "发起二维码签到", "checkin", "POST", "/api/checkin/session/**"),
                createPermission("checkin:validate", "验证签到", "验证学生签到", "checkin", "POST", "/api/checkin/validate"),
                createPermission("checkin:view", "查看签到", "查看签到记录", "checkin", "GET", "/api/checkin/**"),
                
                // 权限管理
                createPermission("permission:manage", "管理权限", "管理角色和权限", "permission", "*", "/api/permissions/**")
            );

            permissionRepository.saveAll(permissions);
            logger.info("初始化权限数据完成");
        }
    }

    private Permission createPermission(String code, String name, String desc, String module, String method, String url) {
        Permission p = new Permission();
        p.setPermCode(code);
        p.setPermName(name);
        p.setPermDesc(desc);
        p.setModule(module);
        p.setHttpMethod(method);
        p.setUrlPattern(url);
        p.setStatus(1);
        return p;
    }

    private void initRolePermissions() {
        if (rolePermissionRepository.count() == 0) {
            Role adminRole = roleRepository.findByRoleCode("admin").orElse(null);
            Role teacherRole = roleRepository.findByRoleCode("teacher").orElse(null);
            Role studentRole = roleRepository.findByRoleCode("student").orElse(null);

            if (adminRole != null) {
                List<Permission> allPerms = permissionRepository.findAll();
                for (Permission perm : allPerms) {
                    RolePermission rp = new RolePermission();
                    rp.setRoleId(adminRole.getId());
                    rp.setPermId(perm.getId());
                    rolePermissionRepository.save(rp);
                }
            }

            if (teacherRole != null) {
                List<Permission> teacherPerms = permissionRepository.findAll().stream()
                    .filter(p -> !p.getPermCode().startsWith("permission:"))
                    .filter(p -> !p.getPermCode().startsWith("user:") || 
                               p.getPermCode().equals("user:view"))
                    .toList();
                for (Permission perm : teacherPerms) {
                    RolePermission rp = new RolePermission();
                    rp.setRoleId(teacherRole.getId());
                    rp.setPermId(perm.getId());
                    rolePermissionRepository.save(rp);
                }
            }

            if (studentRole != null) {
                List<Permission> studentPerms = permissionRepository.findAll().stream()
                    .filter(p -> p.getPermCode().equals("attendance:view") ||
                               p.getPermCode().equals("leave:apply") ||
                               p.getPermCode().equals("leave:view") ||
                               p.getPermCode().equals("checkin:validate"))
                    .toList();
                for (Permission perm : studentPerms) {
                    RolePermission rp = new RolePermission();
                    rp.setRoleId(studentRole.getId());
                    rp.setPermId(perm.getId());
                    rolePermissionRepository.save(rp);
                }
            }

            logger.info("初始化角色权限关联完成");
        }
    }
}