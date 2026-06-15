package com.example.attendance.jpa.repository;

import com.example.attendance.jpa.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {
    
    List<RolePermission> findByRoleId(Long roleId);
    
    List<RolePermission> findByPermId(Long permId);
    
    void deleteByRoleId(Long roleId);
    
    void deleteByPermId(Long permId);
    
    boolean existsByRoleIdAndPermId(Long roleId, Long permId);
    
    @Query("SELECT rp.permId FROM RolePermission rp WHERE rp.roleId IN :roleIds")
    List<Long> findPermIdsByRoleIds(@Param("roleIds") List<Long> roleIds);
}