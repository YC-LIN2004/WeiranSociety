package com.example.Backend.Utils;

public enum AccountStatus {
    // 帳號狀態

    // 啟用
    ACTIVE,

    // 停用
    INACTIVE,

    // 暫時封鎖
    SUSPENDED,

    // 待驗證
    PENDING,

    // 已驗證
    VERIFIED,

    // 已刪除
    DELETED,

    // 鎖定（密碼錯誤）
    LOCKED,

    // 永久封鎖
    BANNED,

}
