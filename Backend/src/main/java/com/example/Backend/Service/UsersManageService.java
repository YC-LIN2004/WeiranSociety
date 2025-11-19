package com.example.Backend.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.Backend.DTO.DTOS.UsersManageDTO;
import com.example.Backend.Entity.Users;
import com.example.Backend.Repository.UsersManageRepository;
import com.example.Backend.Utils.AccountStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsersManageService {

    private final UsersManageRepository usersManageRepository;

    /**
     * 根據使用者ID查詢使用者及其角色資訊
     */
    @Transactional(readOnly = true)
    public UsersManageDTO getUserWithRoles(Long userId) {
        log.info("查詢使用者資訊: userId={}", userId);

        Users user = usersManageRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("找不到使用者: " + userId));

        List<UsersManageDTO.RoleInfo> roles = usersManageRepository.findRolesByUserId(userId);

        return convertToDTO(user, roles);
    }

    /**
     * 根據帳號狀態查詢使用者列表
     */
    @Transactional(readOnly = true)
    public List<UsersManageDTO> getUsersByStatus(AccountStatus status) {
        log.info("根據狀態查詢使用者: status={}", status);

        List<Users> users = usersManageRepository.findByAccountStatus(status);

        return users.stream()
                .map(user -> {
                    List<UsersManageDTO.RoleInfo> roles = usersManageRepository.findRolesByUserId(user.getUserID());
                    return convertToDTO(user, roles);
                })
                .collect(Collectors.toList());
    }

    /**
     * 搜尋使用者 (支援帳號、信箱、電話、真實姓名)
     */
    @Transactional(readOnly = true)
    public List<UsersManageDTO> searchUsers(String keyword) {
        log.info("搜尋使用者: keyword={}", keyword);

        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllUsers();
        }

        List<Users> users = usersManageRepository.searchUsers(keyword.trim());

        return users.stream()
                .map(user -> {
                    List<UsersManageDTO.RoleInfo> roles = usersManageRepository.findRolesByUserId(user.getUserID());
                    return convertToDTO(user, roles);
                })
                .collect(Collectors.toList());
    }

    /**
     * 分頁查詢所有使用者
     */
    @Transactional(readOnly = true)
    public Page<UsersManageDTO> getUsersWithPagination(int page, int size, String sortBy, String direction) {
        log.info("分頁查詢使用者: page={}, size={}, sortBy={}, direction={}",
                page, size, sortBy, direction);

        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<Users> usersPage = usersManageRepository.findAllWithRoles(pageable);

        return usersPage.map(user -> {
            List<UsersManageDTO.RoleInfo> roles = usersManageRepository.findRolesByUserId(user.getUserID());
            return convertToDTO(user, roles);
        });
    }

    /**
     * 根據角色ID查詢使用者列表
     */
    @Transactional(readOnly = true)
    public List<UsersManageDTO> getUsersByRoleId(Long roleId) {
        log.info("根據角色ID查詢使用者: roleId={}", roleId);

        List<Users> users = usersManageRepository.findUsersByRoleId(roleId);

        return users.stream()
                .map(user -> {
                    List<UsersManageDTO.RoleInfo> roles = usersManageRepository.findRolesByUserId(user.getUserID());
                    return convertToDTO(user, roles);
                })
                .collect(Collectors.toList());
    }

    /**
     * 查詢所有使用者
     */
    @Transactional(readOnly = true)
    public List<UsersManageDTO> getAllUsers() {
        log.info("查詢所有使用者");

        List<Users> users = usersManageRepository.findAll();

        return users.stream()
                .map(user -> {
                    List<UsersManageDTO.RoleInfo> roles = usersManageRepository.findRolesByUserId(user.getUserID());
                    return convertToDTO(user, roles);
                })
                .collect(Collectors.toList());
    }

    /**
     * 更新使用者帳號狀態
     */
    @Transactional
    public UsersManageDTO updateUserStatus(Long userId, AccountStatus newStatus) {
        log.info("更新使用者狀態: userId={}, newStatus={}", userId, newStatus);

        Users user = usersManageRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("找不到使用者: " + userId));

        user.setStatus(newStatus);
        Users updatedUser = usersManageRepository.save(user);

        List<UsersManageDTO.RoleInfo> roles = usersManageRepository.findRolesByUserId(userId);

        return convertToDTO(updatedUser, roles);
    }

    /**
     * 刪除使用者
     */
    @Transactional
    public void deleteUser(Long userId) {
        log.info("刪除使用者: userId={}", userId);

        if (!usersManageRepository.existsById(userId)) {
            throw new RuntimeException("找不到使用者: " + userId);
        }

        usersManageRepository.deleteById(userId);
    }

    /**
     * 轉換 Entity 為 DTO
     */
    private UsersManageDTO convertToDTO(Users user, List<UsersManageDTO.RoleInfo> roles) {
        return UsersManageDTO.builder()
                .userID(user.getUserID())
                .account(user.getAccount())
                .email(user.getEmail())
                .phone(user.getPhone())
                .realName(user.getRealname())
                .userStatus(user.getAccountStatus())
                .roles(roles)
                .build();
    }
}