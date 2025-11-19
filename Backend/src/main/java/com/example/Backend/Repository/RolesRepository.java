package com.example.Backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Backend.Entity.Roles;

@Repository
public interface RolesRepository extends JpaRepository<Roles, Long> {
    Roles findByRolename(String rolename);
}