package com.example.attendance.jpa.controller;

import com.example.attendance.common.Result;
import com.example.attendance.jpa.entity.Permission;
import com.example.attendance.jpa.entity.Role;
import com.example.attendance.jpa.service.PermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

    private static final Logger logger = LoggerFactory.getLogger(PermissionController.class);

    @Autowired
    private PermissionService permissionService;

    @GetMapping("/roles")
    public ResponseEntity<Result<List<Role>>> getAllRoles() {
        return ResponseEntity.ok(permissionService.getAllRoles());
    }

    @GetMapping("/roles/{id}")
    public ResponseEntity<Result<Role>> getRoleById(@PathVariable Long id) {
        return ResponseEntity.ok(permissionService.getRoleById(id));
    }

    @GetMapping("/roles/code/{roleCode}")
    public ResponseEntity<Result<Role>> getRoleByCode(@PathVariable String roleCode) {
        return ResponseEntity.ok(permissionService.getRoleByCode(roleCode));
    }

    @PostMapping("/roles")
    public ResponseEntity<Result<Role>> createRole(@RequestBody Role role) {
        return ResponseEntity.ok(permissionService.createRole(role));
    }

    @PutMapping("/roles")
    public ResponseEntity<Result<Role>> updateRole(@RequestBody Role role) {
        return ResponseEntity.ok(permissionService.updateRole(role));
    }

    @DeleteMapping("/roles/{id}")
    public ResponseEntity<Result<Void>> deleteRole(@PathVariable Long id) {
        return ResponseEntity.ok(permissionService.deleteRole(id));
    }

    @GetMapping("/permissions")
    public ResponseEntity<Result<List<Permission>>> getAllPermissions() {
        return ResponseEntity.ok(permissionService.getAllPermissions());
    }

    @GetMapping("/permissions/{id}")
    public ResponseEntity<Result<Permission>> getPermissionById(@PathVariable Long id) {
        return ResponseEntity.ok(permissionService.getPermissionById(id));
    }

    @GetMapping("/permissions/code/{permCode}")
    public ResponseEntity<Result<Permission>> getPermissionByCode(@PathVariable String permCode) {
        return ResponseEntity.ok(permissionService.getPermissionByCode(permCode));
    }

    @PostMapping("/permissions")
    public ResponseEntity<Result<Permission>> createPermission(@RequestBody Permission permission) {
        return ResponseEntity.ok(permissionService.createPermission(permission));
    }

    @PutMapping("/permissions")
    public ResponseEntity<Result<Permission>> updatePermission(@RequestBody Permission permission) {
        return ResponseEntity.ok(permissionService.updatePermission(permission));
    }

    @DeleteMapping("/permissions/{id}")
    public ResponseEntity<Result<Void>> deletePermission(@PathVariable Long id) {
        return ResponseEntity.ok(permissionService.deletePermission(id));
    }

    @PostMapping("/roles/{roleId}/permissions")
    public ResponseEntity<Result<Void>> assignPermissions(@PathVariable Long roleId, @RequestBody List<Long> permIds) {
        return ResponseEntity.ok(permissionService.assignPermissionsToRole(roleId, permIds));
    }

    @GetMapping("/roles/{roleId}/permissions")
    public ResponseEntity<Result<List<Permission>>> getRolePermissions(@PathVariable Long roleId) {
        return ResponseEntity.ok(permissionService.getPermissionsByRole(roleId));
    }
}