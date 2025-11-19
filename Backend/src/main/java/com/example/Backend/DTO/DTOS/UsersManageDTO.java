package com.example.Backend.DTO.DTOS;

import java.time.LocalDateTime;
import java.util.List;

import com.example.Backend.Utils.AccountStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsersManageDTO {
    private Long userID;
    private String account;
    private String email;
    private String phone;
    private String userName;
    private String realName;
    private AccountStatus userStatus;
    private LocalDateTime createdAt;

    // 角色列表 - 不需要 @OneToMany 註解
    private List<RoleInfo> roles;

    /**
     * 角色資訊 - 不需要 @ManyToOne 註解
     */
    @Data
    public static class RoleInfo {
        private Long roleID;
        private String roleName;
        private String descriptions;

        public RoleInfo() {
        }

        public RoleInfo(Long roleID, String roleName, String descriptions) {
            this.roleID = roleID;
            this.roleName = roleName;
            this.descriptions = descriptions;
        }
    }
}