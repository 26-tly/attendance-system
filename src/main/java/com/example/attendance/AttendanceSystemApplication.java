package com.example.attendance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication//这个注解告诉SpringBoot这是应用的主入口

@RestController//这个注解声明这是一个控制器，用于处理HTTP请求
@EnableMethodSecurity



public class AttendanceSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(AttendanceSystemApplication.class, args);
    }
    //处理GET请求，路径为/hello
    @GetMapping("/hello")//接口
    public String hello() {
        return "欢迎来到班级考勤管理系统！";
    }

    @GetMapping("/about")
    public String about() {
        return "开发者：田龙羽\n学号：42411120\n专业：计算机科学与技术";
    }


}
