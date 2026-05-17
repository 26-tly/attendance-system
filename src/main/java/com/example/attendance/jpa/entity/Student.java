package com.example.attendance.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data // 自动生成 Getter/Setter、toString、equals、hashCode

@NoArgsConstructor // 自动生成无参构造
@AllArgsConstructor // 自动生成全参构造
@Entity
@Table(name="student")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String studentNo; // 学号
    private String name;
    private String gender;
    private LocalDate birthDate;
    private String phone;
    // get/set
}
