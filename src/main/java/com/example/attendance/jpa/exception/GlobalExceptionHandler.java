package com.example.attendance.jpa.exception;

import com.example.attendance.common.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Result<?> handleSize() {
        return Result.error("文件不能超过100MB");
    }

    @ExceptionHandler(Exception.class)
    public Result<?> handle(Exception e) {
        return Result.error("系统异常：" + e.getMessage());
    }
}
