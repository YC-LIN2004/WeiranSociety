package com.example.Backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.Backend.Entity.Users;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {

    // 依ID查詢使用者
    // 使用 LEFT JOIN FETCH 一次撈出 userRole 以及 roles
    // JwtAuthFilter 在 token 驗證階段就不會因 LazyInitializationException 爆掉
    @Query("SELECT DISTINCT u FROM Users u " +
            "LEFT JOIN FETCH u.userRole ur " +
            "LEFT JOIN FETCH ur.roles " +
            "WHERE u.userId = :userId")
    Users findByUserId(@Param("userId") Long userId);

    // 依 email 查詢使用者
    Users findByEmail(String email);

    // 依 account 查詢使用者
    Users findByAccount(String account);

    // 依 reset token 查使用者（用於重置密碼時驗證 token）
    Users findByResettoken(String resettoken);

    // 依電話號碼查詢使用者
    Users findByPhone(String phone);

    // 選擇保險：跨資料庫大小寫不敏感
    @Query("SELECT u FROM Users u WHERE LOWER(u.account) = LOWER(:account)")
    Users findByAccountIgnoreCase(@Param("account") String account);

    @Query("SELECT u FROM Users u WHERE LOWER(u.email) = LOWER(:email)")
    Users findByEmailIgnoreCase(@Param("email") String email);

}
