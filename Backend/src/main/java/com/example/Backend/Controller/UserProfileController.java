package com.example.Backend.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.Backend.DTO.DTOS.UserProfileDTO;
import com.example.Backend.Service.UserProfileService;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")

public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

    // 更新基本資料（暱稱、電話、密碼）
    @PutMapping("/update/{account}")
    public Map<String, String> updateProfile(@PathVariable String account, @RequestBody UserProfileDTO dto) {
        return userProfileService.updateProfile(account, dto);
    }

    // 上傳頭貼
    @PostMapping(value = "/upload-avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, String> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Long userId) throws Exception {
        return userProfileService.uploadAvatar(file, userId);
    }

    // 取得使用者資料（顯示個資頁面時載入）
    @GetMapping("/profile/{account}")
    public ResponseEntity<?> getProfile(@PathVariable String account) {
        System.out.println("✅ [Controller] getProfile 執行中, account = " + account);
        Map<String, Object> profile = userProfileService.getProfile(account);
        return ResponseEntity.ok(profile);
    }
}
