package com.example.Backend.DTO.DTOS;

import java.util.Set;

// 角色權限DTO
public class UserRoleDto {
    private Long userId;
    private Set<String> Roles;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Set<String> getRoles() {
        return Roles;
    }

    public void setRoles(Set<String> roles) {
        Roles = roles;
    }

}