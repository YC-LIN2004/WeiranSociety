package com.example.Backend.DTO.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class ResetPasswordRequest {
    // 忘記密碼時輸入的 Email
    @NotBlank(message = "Email 不可空")
    @Email(message = "Email 格式不正確")
    private String email;

    @NotBlank(message = "驗證碼不可空")
    private String code;

    // 新密碼
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{6,20}$", message = "密碼需包含大寫、小寫字母及數字，長度 6~20 碼")
    private String newPassword;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
