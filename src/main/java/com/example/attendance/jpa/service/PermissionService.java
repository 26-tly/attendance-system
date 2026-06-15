package com.example.attendance.jpa.service;

import com.example.attendance.common.Result;
import com.example.attendance.jpa.entity.Permission;
import com.example.attendance.jpa.entity.Role;

import java.util.List;
import java.util.Set;

public interface PermissionService {
    
    Result<List<Role>> getAllRoles();
    
    Result<Role> getRoleById(Long id);
    
    Result<Role> getRoleByCode(String roleCode);
    
    Result<Role> createRole(Role role);
    
    Result<Role> updateRole(Role role);
    
    Result<Void> deleteRole(Long id);
    
    Result<List<Permission>> getAllPermissions();
    
    Result<Permission> getPermissionById(Long id);
    
    Result<Permission> getPermissionByCode(String permCode);
    
    Result<Permission> createPermission(Permission permission);
    
    Result<Permission> updatePermission(Permission permission);
    
    Result<Void> deletePermission(Long id);
    
    Result<Void> assignPermissionsToRole(Long roleId, List<Long> permIds);
    
    Result<List<Permission>> getPermissionsByRole(Long roleId);
    
    Set<String> getPermissionCodesByUserId(Long userId);
    
    boolean hasPermission(Long userId, String permCode);
    
    boolean hasAnyPermission(Long userId, List<String> permCodes);
}