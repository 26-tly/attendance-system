package com.example.attendance.jpa.entity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
@Data // 自动生成 Getter/Setter、toString、equals、hashCode

@NoArgsConstructor // 自动生成无参构造
@AllArgsConstructor // 自动生成全参构造
@Entity
@Table(name="user")
public class User {
    /**
     * 主键：用户id
     * 自增策略，和数据库表结构保持一致
     */
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
}
