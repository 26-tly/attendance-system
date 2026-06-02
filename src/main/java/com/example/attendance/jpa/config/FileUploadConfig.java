package com.example.attendance.jpa.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
@Component
public class FileUploadConfig {
    @Value("${file.upload.path}")
    private String uploadPath;

    @Value("${file.upload.suffix}")
    private String allowSuffix;

    public String getUploadPath() {
        return uploadPath;
    }

    public List<String> getAllowSuffixList() {
        return Arrays.asList(allowSuffix.split(","));
    }
}
