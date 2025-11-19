package com.example.Backend.Repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.Backend.DTO.DTOS.UsersManageDTO;
import com.example.Backend.Entity.Users;
import com.example.Backend.Utils.AccountStatus;

@Repository
public interface UsersManageRepository extends JpaRepository<Users, Long> {

        /**
         * 查詢所有使用者及其角色資訊 (使用 DTO 投影)
         * 使用 r.roleID (大寫 ID) 來匹配 Roles 實體的欄位名稱
         */
        @Query("SELECT new com.example.Backend.DTO.DTOS.UsersManageDTO$RoleInfo(" +
                        "r.roleID, r.rolename, r.descriptions) " +
                        "FROM Users u " +
                        "LEFT JOIN u.userRole ur " +
                        "LEFT JOIN ur.roles r " +
                        "WHERE u.userId = :userId")
        List<UsersManageDTO.RoleInfo> findRolesByUserId(@Param("userId") Long userId);

        /**
         * 根據帳號狀態查詢使用者
         */
        @Query("SELECT DISTINCT u FROM Users u " +
                        "LEFT JOIN FETCH u.userRole ur " +
                        "LEFT JOIN FETCH ur.roles " +
                        "WHERE u.accountStatus = :status")
        List<Users> findByAccountStatus(@Param("status") AccountStatus status);

        /**
         * 搜尋使用者 (支援帳號、信箱、電話、真實姓名模糊查詢)
         */
        @Query("SELECT DISTINCT u FROM Users u " +
                        "LEFT JOIN FETCH u.userRole ur " +
                        "LEFT JOIN FETCH ur.roles " +
                        "WHERE " +
                        "CAST(u.userId AS string) LIKE %:keyword% " +
                        "OR u.account LIKE %:keyword% " +
                        "OR u.email LIKE %:keyword% " +
                        "OR u.phone LIKE %:keyword% " +
                        "OR u.realname LIKE %:keyword%")
        List<Users> searchUsers(@Param("keyword") String keyword);

        /**
         * 分頁查詢所有使用者
         * 使用 countQuery 避免 FETCH JOIN 在 count 查詢時的問題
         */
        @Query(value = "SELECT DISTINCT u FROM Users u " +
                        "LEFT JOIN FETCH u.userRole ur " +
                        "LEFT JOIN FETCH ur.roles", countQuery = "SELECT COUNT(DISTINCT u) FROM Users u")
        Page<Users> findAllWithRoles(Pageable pageable);

        /**
         * 根據角色ID查詢使用者
         * 使用 r.roleID (大寫 ID) 來匹配 Roles 實體的欄位名稱
         */
        @Query("SELECT DISTINCT u FROM Users u " +
                        "JOIN u.userRole ur " +
                        "JOIN ur.roles r " +
                        "WHERE r.roleID = :roleId")
        List<Users> findUsersByRoleId(@Param("roleId") Long roleId);
}