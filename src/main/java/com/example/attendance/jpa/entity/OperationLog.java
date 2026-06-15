package com.example.attendance.jpa.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "operation_log")
public class OperationLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "username", length = 50)
    private String username;

    @Column(name = "operation_type", nullable = false, length = 50)
    private String operationType;

    @Column(name = "module", length = 50)
    private String module;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "operation_time", updatable = false)
    private LocalDateTime operationTime;

    @Column(name = "success")
    private Boolean success;

    @Column(name = "error_message", length = 1000)
    private String errorMessage;

    public OperationLog() {}

    public OperationLog(Long logId, Integer userId, String username, String operationType, String module,
                       String description, String ipAddress, LocalDateTime operationTime, Boolean success, String errorMessage) {
        this.logId = logId;
        this.userId = userId;
        this.username = username;
        this.operationType = operationType;
        this.module = module;
        this.description = description;
        this.ipAddress = ipAddress;
        this.operationTime = operationTime;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    @PrePersist
    void prePersist() {
        this.operationTime = LocalDateTime.now();
        if (this.success == null) {
            this.success = true;
        }
    }

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
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

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public LocalDateTime getOperationTime() {
        return operationTime;
    }

    public void setOperationTime(LocalDateTime operationTime) {
        this.operationTime = operationTime;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public static OperationLogBuilder builder() {
        return new OperationLogBuilder();
    }

    public static class OperationLogBuilder {
        private Long logId;
        private Integer userId;
        private String username;
        private String operationType;
        private String module;
        private String description;
        private String ipAddress;
        private LocalDateTime operationTime;
        private Boolean success;
        private String errorMessage;

        public OperationLogBuilder logId(Long logId) {
            this.logId = logId;
            return this;
        }

        public OperationLogBuilder userId(Integer userId) {
            this.userId = userId;
            return this;
        }

        public OperationLogBuilder username(String username) {
            this.username = username;
            return this;
        }

        public OperationLogBuilder operationType(String operationType) {
            this.operationType = operationType;
            return this;
        }

        public OperationLogBuilder module(String module) {
            this.module = module;
            return this;
        }

        public OperationLogBuilder description(String description) {
            this.description = description;
            return this;
        }

        public OperationLogBuilder ipAddress(String ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }

        public OperationLogBuilder operationTime(LocalDateTime operationTime) {
            this.operationTime = operationTime;
            return this;
        }

        public OperationLogBuilder success(Boolean success) {
            this.success = success;
            return this;
        }

        public OperationLogBuilder errorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public OperationLog build() {
            return new OperationLog(logId, userId, username, operationType, module, description, 
                                  ipAddress, operationTime, success, errorMessage);
        }
    }
}