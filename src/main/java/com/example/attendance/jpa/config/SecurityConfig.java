package com.example.attendance.jpa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration

@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. 配置跨域
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // 2. 放行所有请求
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                // 3. 关闭CSRF（必须，否则POST/OPTIONS请求会被拦截）
                .csrf(csrf -> csrf.disable())
                // 4. 关闭Session（避免浏览器预检请求被Session拦截）
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    // 全局跨域配置，允许所有来源、所有方法、所有头信息
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 允许所有来源（包括你IDE的63342端口）
        configuration.setAllowedOrigins(Arrays.asList("*"));
        // 允许所有HTTP方法（GET/POST/PUT/DELETE/OPTIONS）
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // 允许所有头信息
        configuration.setAllowedHeaders(Arrays.asList("*"));
        // 允许预检请求缓存，减少重复请求
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
