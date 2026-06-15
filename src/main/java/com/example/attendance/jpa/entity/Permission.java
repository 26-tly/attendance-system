package com.example.attendance.jpa.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "sys_permission")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Permission {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "perm_code", unique = true, nullable = false, length = 100)
    private String permCode;
    
    @Column(name = "perm_name", nullable = false, length = 200)
    private String permName;
    
    @Column(name = "perm_desc", length = 500)
    private String permDesc;
    
    @Column(name = "module", length = 50)
    private String module;
    
    @Column(name = "http_method", length = 10)
    private String httpMethod;
    
    @Column(name = "url_pattern", length = 500)
    private String urlPattern;
    
    @Column(name = "parent_id")
    private Long parentId;
    
    @Column(name = "sort_order")
    private Integer sortOrder;
    
    @Column(name = "status", nullable = false)
    private Integer status = 1;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}