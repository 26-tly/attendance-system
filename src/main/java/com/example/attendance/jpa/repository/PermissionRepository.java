package com.example.attendance.jpa.repository;

import com.example.attendance.jpa.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    
    Optional<Permission> findByPermCode(String permCode);
    
    List<Permission> findByStatus(Integer status);
    
    List<Permission> findByModule(String module);
    
    boolean existsByPermCode(String permCode);
}