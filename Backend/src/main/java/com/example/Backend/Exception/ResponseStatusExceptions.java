package com.example.Backend.Exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ResponseStatusExceptions {

    // DTO 驗證錯誤
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fe -> errors.put(fe.getField(), fe.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    // Service 層業務錯誤
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleBusinessError(ResponseStatusException ex) {
        Map<String, String> error = new HashMap<>();
        String reason = ex.getReason();
        if (reason != null) {
            if (reason.contains("帳號"))
                error.put("account", reason);
            else if (reason.contains("Email"))
                error.put("email", reason);
            else if (reason.contains("手機"))
                error.put("phone", reason);
            else if (reason.contains("密碼"))
                error.put("password", reason);
            else if (reason.contains("暱稱"))
                error.put("username", reason);
            else if (reason.contains("姓名"))
                error.put("realname", reason);
            else
                error.put("general", reason);
        } else {
            error.put("general", "發生未知錯誤");
        }
        return ResponseEntity.status(ex.getStatusCode()).body(error);
    }
}