package com.example.Backend.Service;

import com.example.Backend.DTO.DTOS.UserProfileDTO;
import com.example.Backend.Entity.Users;
import com.example.Backend.Repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.*;
import java.time.Instant;
import java.util.*;

@Service
public class UserProfileService {

    private static final Path AVATAR_DIR = Paths.get("src/main/resources/static/uploads/avatars");

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 上傳頭貼
    public Map<String, String> uploadAvatar(MultipartFile file, Long userId) throws Exception {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("請選擇要上傳的圖片");
        }

        Files.createDirectories(AVATAR_DIR);

        // 產生安全檔名
        String original = Objects.requireNonNullElse(file.getOriginalFilename(), "avatar");
        String ext = original.contains(".") ? original.substring(original.lastIndexOf(".")) : ".png";
        String newName = Instant.now().toEpochMilli() + "_" + UUID.randomUUID() + ext;

        try (InputStream in = file.getInputStream()) {
            Files.copy(in, AVATAR_DIR.resolve(newName), StandardCopyOption.REPLACE_EXISTING);
        }

        String avatarUrl = "/uploads/avatars/" + newName;

        Users user = usersRepository.findByUserId(userId);
        if (user == null) {
            throw new IllegalArgumentException("使用者不存在");
        }

        user.setAvatar(avatarUrl);
        usersRepository.save(user);

        return Map.of(
                "message", "頭貼上傳成功",
                "avatarUrl", avatarUrl);
    }

    // 更新個資
    public Map<String, String> updateProfile(String account, UserProfileDTO dto) {
        Users user = usersRepository.findByAccount(account);
        if (user == null) {
            throw new IllegalArgumentException("使用者不存在");
        }

        if (StringUtils.hasText(dto.getUsername()))
            user.setUsername(dto.getUsername());
        if (StringUtils.hasText(dto.getPhone()))
            user.setPhone(dto.getPhone());
        if (StringUtils.hasText(dto.getAvatar()))
            user.setAvatar(dto.getAvatar());

        if (StringUtils.hasText(dto.getPassword())) {
            user.setPwdhash(passwordEncoder.encode(dto.getPassword()));
        }

        usersRepository.save(user);
        return Map.of("message", "個人資料更新成功");
    }

    // 取得使用者資料（顯示時使用）
    public Map<String, Object> getProfile(String account) {
        Users user = usersRepository.findByAccount(account);
        if (user == null) {
            throw new IllegalArgumentException("使用者不存在");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("account", user.getAccount());
        data.put("username", user.getUsername());
        data.put("realname", user.getRealname());
        data.put("email", user.getEmail());
        data.put("phone", user.getPhone());
        data.put("avatar", user.getAvatar()); // 可為 null
        data.put("status", user.getAccountStatus());

        // 避免 LazyInitializationException
        List<String> roles = new ArrayList<>();
        if (user.getUserRole() != null) {
            user.getUserRole().forEach(ur -> {
                if (ur.getRoles() != null && ur.getRoles().getRolename() != null) {
                    roles.add(ur.getRoles().getRolename());
                }
            });
        }
        data.put("roles", roles);

        return data;
    }
}
