package com.example.attendance.jpa.service.impl;

import com.example.attendance.common.Result;
import com.example.attendance.jpa.entity.Permission;
import com.example.attendance.jpa.entity.Role;
import com.example.attendance.jpa.entity.RolePermission;
import com.example.attendance.jpa.entity.User;
import com.example.attendance.jpa.repository.PermissionRepository;
import com.example.attendance.jpa.repository.RolePermissionRepository;
import com.example.attendance.jpa.repository.RoleRepository;
import com.example.attendance.jpa.repository.UserRepository;
import com.example.attendance.jpa.service.PermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PermissionServiceImpl implements PermissionService {

    private static final Logger logger = LoggerFactory.getLogger(PermissionServiceImpl.class);

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private RolePermissionRepository rolePermissionRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Result<List<Role>> getAllRoles() {
        return Result.success(roleRepository.findByStatus(1));
    }

    @Override
    public Result<Role> getRoleById(Long id) {
        Role role = roleRepository.findById(id).orElse(null);
        if (role == null) {
            return Result.error("角色不存在");
        }
        return Result.success(role);
    }

    @Override
    public Result<Role> getRoleByCode(String roleCode) {
        Role role = roleRepository.findByRoleCode(roleCode).orElse(null);
        if (role == null) {
            return Result.error("角色不存在");
        }
        return Result.success(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Role> createRole(Role role) {
        if (roleRepository.existsByRoleCode(role.getRoleCode())) {
            return Result.error("角色编码已存在");
        }
        Role saved = roleRepository.save(role);
        logger.info("创建角色: {}", role.getRoleCode());
        return Result.success(saved);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Role> updateRole(Role role) {
        if (!roleRepository.existsById(role.getId())) {
            return Result.error("角色不存在");
        }
        Role saved = roleRepository.save(role);
        logger.info("更新角色: {}", role.getRoleCode());
        return Result.success(saved);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> deleteRole(Long id) {
        if (!roleRepository.existsById(id)) {
            return Result.error("角色不存在");
        }
        rolePermissionRepository.deleteByRoleId(id);
        roleRepository.deleteById(id);
        logger.info("删除角色: {}", id);
        return Result.success();
    }

    @Override
    public Result<List<Permission>> getAllPermissions() {
        return Result.success(permissionRepository.findByStatus(1));
    }

    @Override
    public Result<Permission> getPermissionById(Long id) {
        Permission permission = permissionRepository.findById(id).orElse(null);
        if (permission == null) {
            return Result.error("权限不存在");
        }
        return Result.success(permission);
    }

    @Override
    public Result<Permission> getPermissionByCode(String permCode) {
        Permission permission = permissionRepository.findByPermCode(permCode).orElse(null);
        if (permission == null) {
            return Result.error("权限不存在");
        }
        return Result.success(permission);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Permission> createPermission(Permission permission) {
        if (permissionRepository.existsByPermCode(permission.getPermCode())) {
            return Result.error("权限编码已存在");
        }
        Permission saved = permissionRepository.save(permission);
        logger.info("创建权限: {}", permission.getPermCode());
        return Result.success(saved);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Permission> updatePermission(Permission permission) {
        if (!permissionRepository.existsById(permission.getId())) {
            return Result.error("权限不存在");
        }
        Permission saved = permissionRepository.save(permission);
        logger.info("更新权限: {}", permission.getPermCode());
        return Result.success(saved);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> deletePermission(Long id) {
        if (!permissionRepository.existsById(id)) {
            return Result.error("权限不存在");
        }
        rolePermissionRepository.deleteByPermId(id);
        permissionRepository.deleteById(id);
        logger.info("删除权限: {}", id);
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> assignPermissionsToRole(Long roleId, List<Long> permIds) {
        if (!roleRepository.existsById(roleId)) {
            return Result.error("角色不存在");
        }
        
        rolePermissionRepository.deleteByRoleId(roleId);
        
        for (Long permId : permIds) {
            if (permissionRepository.existsById(permId)) {
                RolePermission rp = new RolePermission();
                rp.setRoleId(roleId);
                rp.setPermId(permId);
                rolePermissionRepository.save(rp);
            }
        }
        logger.info("为角色 {} 分配权限: {}", roleId, permIds);
        return Result.success();
    }

    @Override
    public Result<List<Permission>> getPermissionsByRole(Long roleId) {
        List<Long> permIds = rolePermissionRepository.findByRoleId(roleId)
                .stream()
                .map(RolePermission::getPermId)
                .collect(Collectors.toList());
        return Result.success(permissionRepository.findAllById(permIds));
    }

    @Override
    public Set<String> getPermissionCodesByUserId(Long userId) {
        User user = userRepository.findById(userId.intValue()).orElse(null);
        if (user == null) {
            return Collections.emptySet();
        }
        
        String roleCode = user.getUserRole();
        Role role = roleRepository.findByRoleCode(roleCode).orElse(null);
        if (role == null) {
            return Collections.emptySet();
        }
        
        return rolePermissionRepository.findByRoleId(role.getId())
                .stream()
                .map(rp -> permissionRepository.findById(rp.getPermId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(Permission::getPermCode)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean hasPermission(Long userId, String permCode) {
        Set<String> userPerms = getPermissionCodesByUserId(userId);
        return userPerms.contains(permCode);
    }

    @Override
    public boolean hasAnyPermission(Long userId, List<String> permCodes) {
        Set<String> userPerms = getPermissionCodesByUserId(userId);
        return permCodes.stream().anyMatch(userPerms::contains);
    }
}