package com.example.Backend.Entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "Roles")
public class Roles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RoleID")
    private Long roleID;

    // 角色名稱
    @NotBlank(message = "角色名稱不可為空")
    @Column(name = "Rolename", nullable = false, unique = true)
    private String rolename;

    // 角色描述
    @Column(name = "Descriptions", length = 100)
    private String descriptions;

    // 與UserRole一對多關聯
    @OneToMany(mappedBy = "roles", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserRoles> userroles = new HashSet<>();

    // 無參數建構子提供給JPA實作
    public Roles() {
    }

    // getter & setter
    public Long getRoleID() {
        return roleID;
    }

    public void setRoleID(Long roleID) {
        this.roleID = roleID;
    }

    public String getRolename() {
        return rolename;
    }

    public void setRolename(String rolename) {
        this.rolename = rolename;
    }

    public String getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(String descriptions) {
        this.descriptions = descriptions;
    }

    public Set<UserRoles> getUserroles() {
        return userroles;
    }

    public void setUserroles(Set<UserRoles> userroles) {
        this.userroles = userroles;
    }

}
