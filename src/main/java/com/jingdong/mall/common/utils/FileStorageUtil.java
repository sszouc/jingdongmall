// src/main/java/com/jingdong/mall/common/utils/FileStorageUtil.java
package com.jingdong.mall.common.utils;

import com.jingdong.mall.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 文件存储工具类
 * 负责文件的保存、删除和验证
 */
@Slf4j
@Component
public class FileStorageUtil {

    @Value("${file.upload-dir:/app/uploads/avatar}")
    private String uploadDir;

    @Value("${file.base-url:http://localhost:8080/uploads/avatar}")
    private String baseUrl;

    private Path uploadBasePath;

    /**
     * Docker环境初始化
     */
    @PostConstruct
    public void init() {
        try {
            log.info("=== Docker文件存储初始化 ===");
            log.info("配置路径: {}", uploadDir);
            log.info("基础URL: {}", baseUrl);

            // 获取容器ID（用于日志）
            String containerId = System.getenv("HOSTNAME");
            if (containerId != null) {
                log.info("容器ID: {}", containerId);
            }

            // 使用绝对路径
            uploadBasePath = Paths.get(uploadDir).toAbsolutePath();
            log.info("绝对路径: {}", uploadBasePath);

            // 创建目录（Docker volume会自动挂载，但目录可能需要创建）
            if (!Files.exists(uploadBasePath)) {
                Files.createDirectories(uploadBasePath);
                log.info("创建Docker容器内目录: {}", uploadBasePath);
            }

            // 测试写入权限
            testWritePermission();

            log.info("Docker文件存储初始化完成");

        } catch (Exception e) {
            log.error("Docker文件存储初始化失败", e);
            // Docker环境下，使用备用路径
            try {
                uploadBasePath = Paths.get("/tmp/uploads/avatar");
                Files.createDirectories(uploadBasePath);
                log.warn("使用备用路径: {}", uploadBasePath);
            } catch (Exception ex) {
                throw new RuntimeException("无法初始化文件存储", ex);
            }
        }
    }

    /**
     * 测试写入权限
     */
    private void testWritePermission() throws IOException {
        Path testFile = uploadBasePath.resolve("test_write.tmp");
        try {
            Files.writeString(testFile, "Docker write test - " + System.currentTimeMillis());
            Files.delete(testFile);
            log.info("写入权限测试通过");
        } catch (Exception e) {
            log.error("写入权限测试失败: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 保存用户头像（Docker优化版）
     */
    public String storeUserAvatar(MultipartFile file, Long userId) {
        try {
            log.info("Docker环境保存头像，用户ID: {}", userId);

            // 1. 验证文件
            validateImageFile(file);

            // 2. 生成文件名
            String fileExtension = getFileExtension(file.getOriginalFilename());
            String uniqueFileName = UUID.randomUUID() + fileExtension;
            String userDir = "user_" + userId;

            // 3. 确保目录存在
            Path targetDir = uploadBasePath.resolve(userDir);
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
                log.info("创建用户目录: {}", targetDir);
            }

            // 4. 保存文件到Docker volume
            Path targetLocation = targetDir.resolve(uniqueFileName);
            file.transferTo(targetLocation);

            log.info("文件保存到Docker volume: {}", targetLocation);
            log.info("文件大小: {} bytes", Files.size(targetLocation));

            // 5. 生成访问URL
            String cleanBaseUrl = baseUrl.endsWith("/") ?
                    baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
            String accessUrl = String.format("%s/%s/%s", cleanBaseUrl, userDir, uniqueFileName);

            log.info("生成访问URL: {}", accessUrl);
            return accessUrl;

        } catch (IOException e) {
            log.error("Docker环境保存文件失败: userId={}", userId, e);
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * Docker环境专用：检查文件是否存在
     */
    public boolean dockerFileExists(String fileUrl) {
        try {
            String relativePath = extractRelativePathFromUrl(fileUrl);
            if (relativePath == null) {
                return false;
            }

            Path filePath = uploadBasePath.resolve(relativePath);
            boolean exists = Files.exists(filePath);

            if (exists) {
                log.debug("Docker volume中文件存在: {}", filePath);
            }

            return exists;
        } catch (Exception e) {
            log.error("检查文件存在失败", e);
            return false;
        }
    }

    /**
     * 提取相对路径（适配Docker URL）
     */
    private String extractRelativePathFromUrl(String url) {
        if (url == null) {
            return null;
        }

        String cleanBaseUrl = baseUrl.endsWith("/") ?
                baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;

        if (!url.startsWith(cleanBaseUrl)) {
            log.warn("URL不匹配baseUrl: url={}, baseUrl={}", url, cleanBaseUrl);
            return null;
        }

        return url.substring(cleanBaseUrl.length() + 1); // +1 跳过斜杠
    }

    /**
     * 删除文件
     *
     * @param fileUrl 文件URL
     * @return 是否删除成功
     */
    public boolean deleteFile(String fileUrl) {
        try {
            log.info("尝试删除文件: {}", fileUrl);

            // 从URL中提取相对路径
            String relativePath = extractRelativePathFromUrl(fileUrl);
            log.info("提取的相对路径: {}", relativePath);

            if (relativePath == null || relativePath.trim().isEmpty()) {
                log.warn("无法从URL中提取相对路径: {}", fileUrl);
                return false;
            }

            // 使用绝对路径
            Path filePath = uploadBasePath.resolve(relativePath);
            log.info("文件物理路径: {}", filePath);

            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("文件删除成功: {}", filePath);
                return true;
            }

            log.warn("文件不存在: {}", filePath);
            return false;

        } catch (IOException e) {
            log.error("删除文件失败: {}", fileUrl, e);
            return false;
        }
    }

    /**
     * 验证图片文件
     */
    private void validateImageFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("文件不能为空");
        }

        // 验证文件大小（5MB以内）
        long maxSize = 5 * 1024 * 1024; // 5MB
        if (file.getSize() > maxSize) {
            throw new IOException("图片大小不能超过5MB");
        }

        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IOException("只能上传图片文件");
        }

        // 验证文件扩展名
        String filename = file.getOriginalFilename();
        if (filename != null) {
            String ext = getFileExtension(filename).toLowerCase();
            if (!ext.matches("\\.(jpg|jpeg|png|gif|bmp|webp)$")) {
                throw new IOException("不支持的文件格式，请上传jpg、png、gif等图片格式");
            }
        }
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return ".jpg"; // 默认扩展名
        }
        return filename.substring(filename.lastIndexOf("."));
    }


    /**
     * 生成头像URL（用于测试或手动生成URL）
     *
     * @param userId   用户ID
     * @param filename 文件名
     * @return 完整的访问URL
     */
    public String generateAvatarUrl(Long userId, String filename) {
        LocalDate today = LocalDate.now();
        String datePath = today.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String userSubDir = "user_" + userId;

        return String.format("%s/%s/%s/%s", baseUrl, datePath, userSubDir, filename);
    }
}