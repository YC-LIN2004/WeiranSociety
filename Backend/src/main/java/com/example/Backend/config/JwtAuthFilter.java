package com.example.Backend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.example.Backend.Entity.Users;
import com.example.Backend.Repository.UsersRepository;
import com.example.Backend.Utils.JwtUtils;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JwtAuthFilter
 * 每次請求都會經過這個 Filter。
 * 功能：
 * 1️⃣ 放行公開 API
 * 2️⃣ 驗證 token 並注入使用者資訊
 * 3️⃣ token 無效時不直接回 403，而是繼續傳遞給後續安全規則
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UsersRepository usersRepository;

    public JwtAuthFilter(JwtUtils jwtUtils, UsersRepository usersRepository) {
        this.jwtUtils = jwtUtils;
        this.usersRepository = usersRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // ✅ 1️⃣ 放行公開路由
        if (path.startsWith("/api/auth/")
                || path.startsWith("/api/public/")
                || path.startsWith("/api/course/")
                || path.startsWith("/api/courses/")
                || path.startsWith("/api/cart/") // ✅ 新增
                || path.startsWith("/api/orders/") // ✅ 新增
                || path.startsWith("/api/payment/")
                || path.startsWith("/api/payment/callback") // ✅ 新增：綠界回呼
                || path.startsWith("/api/payment/return") // ✅ 新增：綠界返回
                || path.startsWith("/uploads/")
                || path.startsWith("/api/uploads/")
                || path.startsWith("/file/")
                || path.startsWith("/static/")) {

            System.out.println("🟢 JwtAuthFilter 放行公開路由：" + path);
            filterChain.doFilter(request, response);
            return;
        }

        // 取得 Header
        final String authHeader = request.getHeader("Authorization");
        String token = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        // 沒有 token → 直接放行
        if (token == null || token.isBlank() || "null".equalsIgnoreCase(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 若有 token 且尚未被認證
        if (SecurityContextHolder.getContext().getAuthentication() == null) {

            if (!jwtUtils.validateToken(token)) {
                System.out.println("⚠️ Token 驗證失敗，但繼續放行：" + token);
                filterChain.doFilter(request, response);
                return;
            }

            Long userId = jwtUtils.getUserIdFromToken(token);
            System.out.println("✅ Token 驗證通過，用戶 ID：" + userId);

            if (userId != null) {
                Users user = usersRepository.findByUserId(userId);

                if (user != null) {
                    List<String> roles = jwtUtils.extractRoleNamesSafely(user);
                    List<SimpleGrantedAuthority> authorities = roles.stream()
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                            .collect(Collectors.toList());

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user, null,
                            authorities);
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
