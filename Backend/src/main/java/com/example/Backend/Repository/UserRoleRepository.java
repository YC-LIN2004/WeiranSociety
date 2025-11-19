package com.example.Backend.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.Backend.Entity.UserRoles;
import com.example.Backend.Entity.Users;

import jakarta.transaction.Transactional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRoles, Long> {

    @Query("SELECT ur.roles.rolename FROM UserRoles ur WHERE ur.users.id = :userId")
    List<String> findUserRolesByUserId(@Param("userId") Long userId);

    @Transactional
    void deleteByUsers(Users users);
}