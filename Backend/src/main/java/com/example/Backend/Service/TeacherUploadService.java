package com.example.Backend.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class TeacherUploadService {

    private final String BASE_PATH = "src/main/resources/static/uploads/";

    // 儲存課程封面圖，回傳可用的 URL
    public String saveCover(MultipartFile file) throws IOException {
        return saveFile(file, "covers");
    }

    // 儲存課程影片檔（或未來預覽用）
    public String saveMedia(MultipartFile file) throws IOException {
        return saveFile(file, "media");
    }

    private String saveFile(MultipartFile file, String folder) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("檔案為空");
        }

        String original = file.getOriginalFilename();
        String ext = "";
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf('.'));
        }

        String filename = System.currentTimeMillis() + "_" + UUID.randomUUID() + ext;

        Path uploadDir = Paths.get(BASE_PATH + folder).toAbsolutePath().normalize();
        Files.createDirectories(uploadDir);

        Path savePath = uploadDir.resolve(filename);
        Files.copy(file.getInputStream(), savePath, StandardCopyOption.REPLACE_EXISTING);

        // 回傳前端可直接使用的靜態 URL
        return "/uploads/" + folder + "/" + filename;
    }
}
