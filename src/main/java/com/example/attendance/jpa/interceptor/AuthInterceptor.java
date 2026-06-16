package com.example.attendance.jpa.interceptor;

import com.example.attendance.jpa.service.PermissionService;
import com.example.attendance.jpa.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(AuthInterceptor.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PermissionService permissionService;

    private static final String[] PUBLIC_PATHS = {
            "/login", "/register", "/api/auth/**", "/error",
            "/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**",
            "/user/login", "/user/register", "/user/checkUsername",
            "/api/server/**",
            "/student_qrcode"
    };

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestUri = request.getRequestURI();
        String method = request.getMethod();

        if (isPublicPath(requestUri)) {
            return true;
        }

        String token = extractToken(request);
        if (token == null) {
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "未登录，请先登录");
            return false;
        }

        if (!jwtUtil.validateToken(token)) {
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "登录已过期，请重新登录");
            return false;
        }

        Long userId = jwtUtil.getUserIdFromToken(token);
        String role = jwtUtil.getRoleFromToken(token);

        if (userId == null || role == null) {
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "token无效");
            return false;
        }

        request.setAttribute("userId", userId);
        request.setAttribute("role", role);

        if (!checkRolePermission(requestUri, method, role, userId)) {
            sendErrorResponse(response, HttpStatus.FORBIDDEN, "权限不足");
            return false;
        }

        return true;
    }

    private boolean isPublicPath(String uri) {
        for (String path : PUBLIC_PATHS) {
            if (uri.startsWith(path.replace("/**", ""))) {
                if (path.endsWith("/**")) {
                    return true;
                }
                if (uri.equals(path)) {
                    return true;
                }
            }
        }
        return false;
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private boolean checkRolePermission(String uri, String method, String role, Long userId) {
        if ("admin".equalsIgnoreCase(role)) {
            return true;
        }

        if ("teacher".equalsIgnoreCase(role)) {
            return uri.startsWith("/api/teacher/") || 
                   uri.startsWith("/api/course") || 
                   uri.startsWith("/api/student") ||
                   uri.startsWith("/api/checkin") ||
                   uri.startsWith("/api/course-student") ||
                   uri.startsWith("/api/attendance-management") ||
                   uri.startsWith("/api/leave") ||
                   uri.startsWith("/api/attendance") ||
                   uri.startsWith("/api/user") ||
                   uri.startsWith("/api/dashboard");
        }

        if ("student".equalsIgnoreCase(role)) {
            return uri.startsWith("/api/student/") || 
                   uri.startsWith("/api/attendance/user") || 
                   uri.startsWith("/api/leave/user") ||
                   uri.startsWith("/api/checkin/validate") ||
                   uri.startsWith("/api/checkin/session") ||
                   uri.startsWith("/api/attendance") ||
                   uri.startsWith("/api/course-student/user");
        }

        return false;
    }

    private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> error = new HashMap<>();
        error.put("code", status.value());
        error.put("msg", message);

        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(error));
    }
}