package com.example.Backend.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Backend.DTO.Request.ForgotPasswordRequest;
import com.example.Backend.DTO.Request.LoginRequest;
import com.example.Backend.DTO.Request.RegisterRequest;
import com.example.Backend.DTO.Request.ResetPasswordRequest;
import com.example.Backend.DTO.Response.LoginResponse;
import com.example.Backend.Service.AuthService;

import jakarta.validation.Valid;

// 測試時需修改api
@CrossOrigin(origins = "*")

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    // 注入 AuthService
    private AuthService authService;

    // 註冊
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok("註冊成功！請至 Email 收取驗證碼並於 15 分鐘內完成驗證。");
    }

    // 使用者帳號驗證
    @PostMapping("/verify")
    public ResponseEntity<?> verifyAccount(@RequestParam String account, @RequestParam String code) {
        try {
            String message = authService.verifyAccount(account, code);
            return ResponseEntity.ok(Map.of("message", message));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    // 登入
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            // 呼叫 Service 登入
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    // 忘記密碼
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.sendResetCode(request);
        return ResponseEntity.ok("驗證碼已寄出，請查收信箱");
    }

    // 更新密碼
    @PostMapping("/verify-reset")
    public ResponseEntity<String> verifyResetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.verifyAndResetPassword(request);
        return ResponseEntity.ok("密碼已成功更新，請重新登入");
    }

    // SPA fallback
    // 所有非靜態資源的請求都回到 index.html 交給前端路由去處理
    @GetMapping("/{path:[^\\.]*}")
    public String forward() {

        return "forward:/index.html";
    }

}
