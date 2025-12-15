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

    @Value("${file.upload-dir:/root/uploads/avatar}")
    private String uploadDir;

    @Value("${file.base-url:http://localhost:8080/root/uploads/avatar}")
    private String baseUrl;

    private Path uploadBasePath;

    /**
     * 初始化，确保使用Linux绝对路径
     */
    @PostConstruct
    public void init() {
        try {
            // 标准化路径，确保是Linux绝对路径
            uploadBasePath = Paths.get(uploadDir).toAbsolutePath().normalize();
            uploadDir = uploadBasePath.toString(); // 更新为标准化后的路径

            log.info("Linux服务器上传根目录: {}", uploadDir);

            if (!Files.exists(uploadBasePath)) {
                Files.createDirectories(uploadBasePath);
                log.info("创建上传目录: {}", uploadDir);
            }

            // 检查目录权限
            if (!Files.isWritable(uploadBasePath)) {
                log.error("上传目录没有写权限: {}", uploadDir);
                throw new IOException("上传目录没有写权限");
            }

            log.info("文件上传目录初始化完成，路径: {}", uploadDir);

        } catch (IOException e) {
            log.error("初始化上传目录失败: {}", uploadDir, e);
            throw new RuntimeException("文件存储初始化失败", e);
        }
    }

    /**
     * 保存用户头像文件
     *
     * @param file   上传的文件
     * @param userId 用户ID
     * @return 文件访问URL
     */
    public String storeUserAvatar(MultipartFile file, Long userId) {
        try {
            log.info("开始保存头像文件，用户ID: {}", userId);

            // 1. 验证文件
            validateImageFile(file);

            // 2. 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            log.info("原始文件名: {}", originalFilename);

            String fileExtension = getFileExtension(originalFilename);
            String uniqueFileName = UUID.randomUUID() + fileExtension;
            log.info("新文件名: {}", uniqueFileName);

            // 3. 创建用户专属目录（可选）
            // 可以直接保存在 /uploads/avatar 下，也可以按用户分目录
            String userDir = "user_" + userId;
            Path targetDir = uploadBasePath.resolve(userDir);

            log.info("目标目录: {}", targetDir.toString());

            // 4. 创建目录（如果不存在）
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
                log.info("创建用户目录: {}", targetDir);
            }

            // 5. 保存文件到Linux服务器
            Path targetLocation = targetDir.resolve(uniqueFileName);
            log.info("完整保存路径: {}", targetLocation.toString());

            // 使用transferTo的Path版本（更安全）
            file.transferTo(targetLocation);

            log.info("头像文件保存成功: userId={}, size={} bytes, path={}",
                    userId, file.getSize(), targetLocation);

            // 6. 生成访问URL
            String accessUrl = String.format("%s/%s/%s", baseUrl, userDir, uniqueFileName);
            log.info("文件访问URL: {}", accessUrl);

            return accessUrl;

        } catch (IOException e) {
            log.error("保存头像文件失败: userId={}, error={}", userId, e.getMessage(), e);
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }
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
     * 从URL中提取相对路径
     */
    private String extractRelativePathFromUrl(String url) {
        if (url == null) {
            return null;
        }

        log.debug("开始提取相对路径, url={}, baseUrl={}", url, baseUrl);

        // 清理baseUrl（移除末尾斜杠）
        String cleanBaseUrl = baseUrl.endsWith("/") ?
                baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;

        log.debug("清理后的baseUrl: {}", cleanBaseUrl);

        // 检查URL是否以baseUrl开头
        if (!url.startsWith(cleanBaseUrl)) {
            log.warn("URL不匹配baseUrl: url={}, baseUrl={}", url, cleanBaseUrl);
            return null;
        }

        // 提取相对路径（移除baseUrl部分和开头的斜杠）
        String relativePath = url.substring(cleanBaseUrl.length());
        if (relativePath.startsWith("/")) {
            relativePath = relativePath.substring(1);
        }

        log.debug("提取到的相对路径: {}", relativePath);
        return relativePath;
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