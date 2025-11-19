package com.example.Backend.DTO.Request;

// 登入請求DTO
public class LoginRequest {

    private String account;

    // 明碼(Service處理加密)
    private String password;

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

}
