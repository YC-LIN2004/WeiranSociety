package com.example.Backend.Service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.Backend.DTO.DTOS.UserRoleDto;
import com.example.Backend.Entity.Roles;
import com.example.Backend.Entity.UserRoles;
import com.example.Backend.Entity.Users;
import com.example.Backend.Repository.RolesRepository;
import com.example.Backend.Repository.UserRoleRepository;
import com.example.Backend.Repository.UsersRepository;

@Service
public class RoleAssignmentService {

    @Autowired
    // 注入 UsersRepository 查使用者
    private UsersRepository usersRepository;

    // 注入 RolesRepository 查角色
    @Autowired
    private RolesRepository rolesRepository;
    // 注入 UserRoleRepository 新增或刪除相關關聯
    @Autowired
    private UserRoleRepository userRoleRepository;

    // 指派角色給使用者核心方法
    // 支援roles欄位為roleId或rolename
    // 以交易包裝確保一致性
    @Transactional
    public void assignRolesToUser(UserRoleDto dto) {
        // 依userId查詢使用者
        Users user = usersRepository.findByUserId(dto.getUserId());
        if (user == null) {
            // 使用 ResponseStatusException 讓全域 exception handler 統一處理
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "使用者不存在 (userId=" + dto.getUserId() + ")");
        }

        // 刪除該使用者既有的 UserRole 關聯（避免重複或殘留）
        userRoleRepository.deleteByUsers(user);

        // 若前端沒有傳 roles 或為空，代表只是清除所有角色，直接 return
        if (dto.getRoles() == null || dto.getRoles().isEmpty()) {
            // 刪除舊關聯
            return;
        }
        // 準備要一次存進 DB 的 UserRole 實體集合
        Set<UserRoles> newUserRoles = new HashSet<>();

        // 逐個解析前端傳來的 role 標識（可能是 id 或名稱）
        for (String roleName : dto.getRoles()) {
            // 依名稱查找角色
            Roles roles = rolesRepository.findByRolename(roleName);
            if (roles == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "角色不存在:" + roleName);
            }
            UserRoles ur = new UserRoles();
            // 設定使用者物件
            ur.setUsers(user);
            // 設定角色物件
            ur.setRoles(roles);
            // 批次儲存所有新的 UserRole關聯
            newUserRoles.add(ur);
        }
        // 批次儲存所有新的 UserRole 關聯
        userRoleRepository.saveAll(newUserRoles);
    }

    // 取得使用者角色名稱 Set
    public Set<String> getUserRoles(Long userId) {
        Users user = usersRepository.findByUserId(userId);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "使用者不存在 (userId=" + userId + ")");
        }

        // 直接透過 Repository 回傳的 List<String> 轉 Set
        return new HashSet<>(userRoleRepository.findUserRolesByUserId(userId));
    }
}