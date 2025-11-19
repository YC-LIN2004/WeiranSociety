package com.example.Backend.DTO.Response;

// JWT回應DTO
public class JwtReaponse {
    // JWT 字串
    private String token;

    // 無參數建構子，供 JSON 反序列化使用
    public JwtReaponse() {
    }

    // 有參數建構子，方便回傳 token
    public JwtReaponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
