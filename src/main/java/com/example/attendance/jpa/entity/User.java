package com.example.attendance.jpa.entity;

import jakarta.persistence.*;

@Entity
@Table(name="[user]")
public class User {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="user_id")
    private Integer userId;

    @Column(name="username",nullable = false,unique = true,length = 50)        
    private String username;

    @Column(name="password",nullable = false,length = 100)
    private String password;

    @Column(name="user_role",length = 20)
    private String userRole;

    public User() {}

    public User(Integer userId, String username, String password, String userRole) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.userRole = userRole;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }
}