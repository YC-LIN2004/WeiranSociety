package com.example.Backend.Entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.example.Backend.Utils.AccountStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "Users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "Email"),
        @UniqueConstraint(columnNames = "Phone")
})
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler", "teacherProfile" }) // ✅ 防止遞迴與 Lazy 問題
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserID")
    private Long userId;

    @Column(name = "Account", nullable = false, unique = true, length = 50)
    private String account;

    // 暱稱
    @Column(name = "Username", nullable = false, unique = true, length = 50)
    private String username;

    // 真實姓名
    @Column(name = "Realname", nullable = false, length = 50)
    private String realname;

    @Column(name = "Pwdhash", nullable = false, length = 256)
    private String pwdhash;

    @Column(name = "Email", nullable = false, unique = true, length = 200)
    private String email;

    @Column(name = "Phone", nullable = false, length = 20)
    private String phone;

    @Column(name = "Avatar", nullable = true, length = 255)
    private String avatar;

    // 帳號狀態預設為待驗證(PENDING)
    @Enumerated(EnumType.STRING)
    @Column(name = "Userstatus", nullable = false, length = 20)
    private AccountStatus accountStatus = AccountStatus.PENDING;

    // 存取忘記密碼功能回傳的Token
    @Column(name = "Resettoken", length = 100)
    private String resettoken;

    // 存取回傳Token的過期時間
    @Column(name = "Resettokenexpiry")
    private LocalDateTime resettokenExpiry;

    // 多重身分關聯
    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserRoles> userRole = new HashSet<>();

    // 無參數建構子提供給JPA實作
    public Users() {
    }

    public Users(Long userId) {
        this.userId = userId;
    }

    // ===== Getter / Setter =====
    public Long getUserID() {
        return userId;
    }

    public void setUserID(Long userId) {
        this.userId = userId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
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

    public String getPwdhash() {
        return pwdhash;
    }

    public void setPwdhash(String pwdhash) {
        this.pwdhash = pwdhash;
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

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public void setStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }

    public String getResettoken() {
        return resettoken;
    }

    public void setResettoken(String resettoken) {
        this.resettoken = resettoken;
    }

    public LocalDateTime getResetTokenExpiry() {
        return resettokenExpiry;
    }

    public void setResetTokenExpiry(LocalDateTime resetTokenExpiry) {
        this.resettokenExpiry = resetTokenExpiry;
    }

    public Set<UserRoles> getUserRole() {
        return userRole;
    }

    public void setUserRole(Set<UserRoles> userRole) {
        this.userRole = userRole;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
