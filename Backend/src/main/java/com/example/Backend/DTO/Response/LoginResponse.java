package com.example.Backend.DTO.Response;

import java.util.List;

public class LoginResponse {

    private String token;
    private Long userId;
    private String account;
    private String username;
    private String email;
    private String avatar;
    private List<String> roles;

    // === Constructor ===
    public LoginResponse(String token, Long userId, String account, String username, String email, String avatar,
            List<String> roles) {
        this.token = token;
        this.userId = userId;
        this.account = account;
        this.username = username;
        this.email = email;
        this.avatar = avatar;
        this.roles = roles;
    }

    // === Getters ===
    public String getToken() {
        return token;
    }

    public Long getUserId() {
        return userId;
    }

    public String getAccount() {
        return account;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getAvatar() {
        return avatar;
    }

    public List<String> getRoles() {
        return roles;
    }
}
