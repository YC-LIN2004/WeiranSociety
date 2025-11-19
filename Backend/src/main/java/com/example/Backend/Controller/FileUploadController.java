package com.example.Backend.Controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.*;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "*") // 允許前端跨域請求
public class FileUploadController {

    // ✅ 指定存放 Teacher 證書的資料夾
    private static final Path SAVE_DIR = Paths.get("src/main/resources/static/uploads/TeacherUpload");

    /**
     * 📤 上傳老師證書檔案（PDF 或圖片）
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, String> uploadCertificate(@RequestParam("file") MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("上傳檔案為空，請重新選擇。");
        }

        // 確保目錄存在
        Files.createDirectories(SAVE_DIR);

        // 取得原始檔名（安全清理）
        String originalName = Objects.requireNonNullElse(file.getOriginalFilename(), "");
        originalName = StringUtils.cleanPath(originalName);

        // 副檔名
        String ext = "";
        int dot = originalName.lastIndexOf('.');
        if (dot >= 0 && dot < originalName.length() - 1) {
            ext = originalName.substring(dot);
        }

        // 產生唯一檔名：時間戳 + UUID
        String newFileName = Instant.now().toEpochMilli() + "_" + UUID.randomUUID() + ext;

        // 寫入檔案
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, SAVE_DIR.resolve(newFileName), StandardCopyOption.REPLACE_EXISTING);
        }

        // ✅ 回傳前端可直接訪問的 URL
        String fileUrl = "/uploads/TeacherUpload/" + newFileName;

        return Map.of(
                "success", "true",
                "url", fileUrl,
                "message", "上傳成功！");
    }

    /**
     * 📥 下載或預覽已上傳的證書檔案
     */
    @GetMapping("/view/{filename:.+}")
    public ResponseEntity<Resource> viewCertificate(@PathVariable String filename) {
        try {
            filename = StringUtils.cleanPath(filename);
            Path filePath = SAVE_DIR.resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            // 嘗試自動偵測 Content-Type
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
