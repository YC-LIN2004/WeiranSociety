package com.example.Backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

// 全域安全設定檔
@Configuration
// 啟用 Spring Security 的 Web 層安全機制
@EnableWebSecurity
// 啟用 @PreAuthorize、角色權限驗證
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    // 密碼加密方式（BCrypt）
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // 啟用跨來源設定（CORS）
                .cors(Customizer.withDefaults())
                // 禁用 CSRF，避免非表單類 POST 被擋
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // 登入、註冊、忘記密碼這些不需驗證
                        .requestMatchers(
                                "/api/auth/**",
                                "/api/public/**",
                                "/api/course/**",
                                "/api/courses/**",
                                "/api/cart/**",
                                "/uploads/**",
                                "/api/uploads/**",
                                "/static/**",
                                "/file/**",
                                "/api/orders/**",
                                "/api/payment/**",
                                "/api/file/**",
                                "/api/users/**",
                                "/api/teacher/**")
                        .permitAll()

                        // ✅ 需要登入才能建立付款
                        .requestMatchers("/api/payment/create").authenticated()

                        // 登入即可操作個人資料（學生、老師、管理員皆可）
                        // .requestMatchers("/api/user/**").permitAll()

                        // 其他路徑一律需要登入授權

                        // // 登入即可操作個人資料（學生、老師、管理員皆可）
                        // .requestMatchers("/api/user/**").permitAll()

                        .anyRequest().permitAll()
                // .authenticated())
                )
                // 把 JWT 驗證過濾器放在 Spring Security 內建驗證前執行
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // CORS 設定，允許前端（Vue）跨來源訪問
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 開發階段允許所有來源；正式上線改為指定網域
        configuration.addAllowedOriginPattern("*");
        configuration.setAllowCredentials(true);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
