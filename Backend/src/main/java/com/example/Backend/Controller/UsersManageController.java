package com.example.Backend.Controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Backend.DTO.DTOS.UsersManageDTO;
import com.example.Backend.Service.UsersManageService;
import com.example.Backend.Utils.AccountStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/users/manage")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UsersManageController {

    private final UsersManageService usersManageService;

    @GetMapping("/page")
    public ResponseEntity<Page<UsersManageDTO>> getUsersWithPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "userId") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Page<UsersManageDTO> usersPage = usersManageService.getUsersWithPagination(page, size, sortBy, direction);
        return ResponseEntity.ok(usersPage);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UsersManageDTO> getUserById(@PathVariable Long userId) {
        UsersManageDTO user = usersManageService.getUserWithRoles(userId);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<List<UsersManageDTO>> getAllUsers() {
        List<UsersManageDTO> users = usersManageService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<UsersManageDTO>> getUsersByStatus(@PathVariable AccountStatus status) {
        List<UsersManageDTO> users = usersManageService.getUsersByStatus(status);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/role/{roleId}")
    public ResponseEntity<List<UsersManageDTO>> getUsersByRoleId(@PathVariable Long roleId) {
        List<UsersManageDTO> users = usersManageService.getUsersByRoleId(roleId);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/search")
    public ResponseEntity<List<UsersManageDTO>> searchUsers(@RequestParam String keyword) {
        List<UsersManageDTO> users = usersManageService.searchUsers(keyword);
        return ResponseEntity.ok(users);
    }

    @PostMapping("/{userId}/status")
    public ResponseEntity<UsersManageDTO> updateUserStatus(
            @PathVariable Long userId,
            @RequestParam String status) {

        log.info("更新狀態: userId={}, status={}", userId, status);
        AccountStatus accountStatus = AccountStatus.valueOf(status);
        UsersManageDTO updatedUser = usersManageService.updateUserStatus(userId, accountStatus);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        usersManageService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}