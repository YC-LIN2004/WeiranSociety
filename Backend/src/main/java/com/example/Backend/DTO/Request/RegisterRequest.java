package com.example.Backend.DTO.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

// 註冊請求DTO
public class RegisterRequest {
    @NotBlank(message = "請輸入帳號")
    @Pattern(regexp = "^[a-zA-Z0-9_]{4,20}$", message = "帳號只能包含英文、數字、底線，4~20 碼")
    private String account;

    // 明碼(Service處理加密)
    @NotBlank(message = "請輸入密碼")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{6,20}$", message = "密碼需包含大寫、小寫字母及數字，長度 6~20 碼")
    private String password;

    @NotBlank(message = "請輸入暱稱")
    private String username;

    @NotBlank(message = "請輸入真實姓名")
    private String realname;

    @NotBlank(message = "請輸入Email")
    @Pattern(
            // 正則說明：
            // ^ : 開頭
            // [A-Za-z0-9._%+-]+ : 本地部分 (a-zA-Z0-9 和 . _ % + -)
            // @ : 必須有 @
            // [A-Za-z0-9.-]+ : domain name
            // \. : 點
            // [A-Za-z]{2,} : domain 後綴至少 2 個字母（.com, .net, .org）
            // $ : 結尾
            regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "Email格式錯誤，請輸入正確的電子郵件")
    private String email;

    @NotBlank(message = "請輸入手機號碼")
    @Pattern(regexp = "^09\\d{8}$", message = "手機號碼格式錯誤")
    private String phone;

    // 前端傳回驗證碼
    private String phoneVerificationCode;

    // getter setter
    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhoneVerificationCode() {
        return phoneVerificationCode;
    }

    public void setPhoneVerificationCode(String phoneVerificationCode) {
        this.phoneVerificationCode = phoneVerificationCode;
    }

}
