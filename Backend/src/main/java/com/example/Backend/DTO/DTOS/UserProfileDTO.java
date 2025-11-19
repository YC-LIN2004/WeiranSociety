package com.example.Backend.DTO.DTOS;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

// 個人資料DTO 
public class UserProfileDTO {

    // 只顯示不可修改
    private String account;
    private String email;
    private String realname;

    // 可修改
    @NotBlank(message = "請輸入密碼")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{6,20}$", message = "密碼需包含大寫、小寫字母及數字，長度 6~20 碼")
    private String password;

    @NotBlank(message = "請輸入暱稱")
    private String username;

    @NotBlank(message = "請輸入手機號碼")
    @Pattern(regexp = "^09\\d{8}$", message = "手機號碼格式錯誤")
    private String phone;

    @Column(name = "Avatar", nullable = true, length = 255)
    private String avatar;

    // getter & setter
    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

}