package com.example.Backend.Utils;

import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.Backend.Entity.UserRoles;
import com.example.Backend.Entity.Users;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;

/**
 * JwtUtils
 * 用於：
 * - 產生 JWT（登入成功時）
 * - 驗證與解析 JWT（Filter 用）
 * - 提取使用者角色資訊
 */
@Component
public class JwtUtils {

    // 從 application.yml 讀取密鑰字串與 token 有效時間
    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms}")
    private long jwtExpirationMs;

    // 加密用金鑰
    private Key secretKey;

    // 初始化金鑰時自動處理 Base64 或純字串格式
    @PostConstruct
    public void init() {
        byte[] keyBytes;
        try {
            // 嘗試將密鑰字串作為 Base64 解碼
            keyBytes = Base64.getDecoder().decode(jwtSecret);
        } catch (IllegalArgumentException e) {
            // 若非 Base64 字串，則直接取原始 bytes
            keyBytes = jwtSecret.getBytes();
        }
        secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 建立 JWT token
     * 
     * @param users 使用者資料物件
     * @return JWT token 字串
     */
    public String generateJwtToken(Users users) {
        Claims claims = Jwts.claims().setSubject(String.valueOf(users.getUserID()));

        // 加入角色資訊至 payload
        List<String> roles = extractRoleNamesSafely(users);
        claims.put("roles", roles);

        // 建立完整 token
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * 從 token 解析出使用者 ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        if (claims == null)
            return null;
        return Long.parseLong(claims.getSubject());
    }

    /**
     * 驗證 token 是否有效
     * 
     * @return true = 有效, false = 無效或過期
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims != null && claims.getExpiration() != null && claims.getExpiration().after(new Date());
        } catch (Exception e) {
            System.out.println("JWT 驗證失敗：" + e.getMessage());
            return false;
        }
    }

    /**
     * 解析 token，回傳其中的 Claims 主體
     */
    private Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            // 顯示具體解析錯誤原因（方便 Debug）
            System.out.println("JWT 解析錯誤：" + e.getMessage());
            return null;
        }
    }

    /**
     * 從 Users 物件中安全提取角色名稱清單
     */
    public List<String> extractRoleNamesSafely(Users users) {
        if (users == null)
            return Collections.emptyList();

        Object raw = users.getUserRole();
        if (raw == null)
            return Collections.emptyList();

        if (raw instanceof Collection<?>) {
            return ((Collection<?>) raw).stream()
                    .filter(Objects::nonNull)
                    .map(obj -> {
                        if (obj instanceof UserRoles ur && ur.getRoles() != null) {
                            return ur.getRoles().getRolename();
                        } else {
                            return obj.toString();
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        if (raw instanceof UserRoles ur && ur.getRoles() != null) {
            return Collections.singletonList(ur.getRoles().getRolename());
        }

        return Collections.singletonList(raw.toString());
    }

    /**
     * 從 HttpServletRequest 解析 Authorization Header 取得 Token
     * Authorization: Bearer <token>
     */
    public String getTokenFromRequest(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7); // 去掉 "Bearer "
        }
        return null;
    }

    /**
     * 直接從 Request 取出 userId（簡化 Service 程式碼）
     */
    public Long getUserIdFromRequest(HttpServletRequest request) {
        String token = getTokenFromRequest(request);

        if (token == null)
            return null;

        return getUserIdFromToken(token);
    }

}
