package com.jingdong.mall.service.impl;

import com.jingdong.mall.common.exception.BusinessException;
import com.jingdong.mall.common.exception.ErrorCode;
import com.jingdong.mall.common.utils.JwtUtil;
import com.jingdong.mall.mapper.UserMapper;
import com.jingdong.mall.model.dto.request.AdminCreateRequest;
import com.jingdong.mall.model.dto.response.AdminCreateResponse;
import com.jingdong.mall.model.dto.response.AdminListResponse;
import com.jingdong.mall.model.entity.User;
import com.jingdong.mall.service.AdminManagementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class AdminManagementServiceImpl implements AdminManagementService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    // 数字字符集
    private static final String NUMBERS = "0123456789";
    // 字母数字字符集
    private static final String ALPHANUMERIC = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdminCreateResponse createAdmin(Long currentUserId, Integer currentUserRole, AdminCreateRequest request) {
        // 1. 权限验证：只有超级管理员(role=2)可以创建管理员
        validatePermission(currentUserRole);

        // 2. 验证手机号是否已存在
        validatePhoneExists(request.getPhone());

        try {
            // 3. 生成用户名（手机号后6位，如果重复则追加随机数）
            String username = generateUsername(request.getPhone());

            // 4. 创建用户实体
            User newAdmin = buildAdminUser(request, username);

            // 5. 保存到数据库
            int result = userMapper.insert(newAdmin);
            if (result <= 0) {
                log.error("创建管理员失败，手机号：{}", request.getPhone());
                throw new BusinessException(ErrorCode.ADMIN_OPERATION_FAILED, "创建管理员失败");
            }

            // 6. 构建响应
            return buildAdminCreateResponse(newAdmin);

        } catch (BusinessException e) {
            // 业务异常直接抛出
            throw e;
        } catch (Exception e) {
            log.error("创建管理员发生系统异常，手机号：{}", request.getPhone(), e);
            throw new BusinessException(ErrorCode.ADMIN_OPERATION_FAILED, "系统异常，创建管理员失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAdmin(Long currentUserId, Integer currentUserRole, Long targetAdminId) {
        // 1. 权限验证：只有超级管理员(role=2)可以删除管理员
        validatePermission(currentUserRole);

        try {
            // 2. 验证目标用户是否存在且是管理员
            User targetAdmin = validateTargetAdmin(targetAdminId);

            // 3. 验证不能删除自己
            validateNotSelf(currentUserId, targetAdminId);

            // 4. 验证不能删除超级管理员
            validateNotSuperAdmin(targetAdmin);

            // 5. 检查是否至少保留一个管理员（可选，根据业务需求）
            //validateMinimumAdmins();

            // 6. 删除管理员
            int result = userMapper.deleteAdminUser(targetAdminId);
            if (result <= 0) {
                log.error("删除管理员失败，目标管理员ID：{}，操作人ID：{}", targetAdminId, currentUserId);
                throw new BusinessException(ErrorCode.ADMIN_OPERATION_FAILED, "删除管理员失败");
            }

            // 7. 记录日志
            log.info("管理员删除成功，操作人：{}，被删除的管理员ID：{}，用户名：{}",
                    currentUserId, targetAdminId, targetAdmin.getUsername());

        } catch (BusinessException e) {
            // 业务异常直接抛出
            throw e;
        } catch (Exception e) {
            log.error("删除管理员发生系统异常，目标管理员ID：{}，操作人ID：{}", targetAdminId, currentUserId, e);
            throw new BusinessException(ErrorCode.ADMIN_OPERATION_FAILED, "系统异常，删除管理员失败");
        }
    }

    @Override
    public AdminListResponse getAdminList(Long currentUserId, Integer currentUserRole) {
        // 1. 权限验证：只有超级管理员(role=2)可以获取管理员列表
        validateSuperAdminPermission(currentUserRole);

        try {
            // 2. 查询管理员列表
            List<Map<String, Object>> adminMaps = userMapper.selectAllAdmins();

            // 3. 统计管理员数量
            Integer total = userMapper.countAdmins();

            // 4. 转换为AdminListResponse
            AdminListResponse response = convertToAdminListResponse(adminMaps, total);

            // 5. 记录日志（可选，生产环境可改为debug级别）
            log.debug("超级管理员获取管理员列表成功，操作人ID：{}，管理员数量：{}", currentUserId, total);

            return response;

        } catch (BusinessException e) {
            // 业务异常直接抛出
            throw e;
        } catch (Exception e) {
            log.error("获取管理员列表发生系统异常，操作人ID：{}", currentUserId, e);
            throw new BusinessException(ErrorCode.ADMIN_OPERATION_FAILED, "获取管理员列表失败");
        }
    }

    /**
     * 验证权限：只有超级管理员可以获取管理员列表
     */
    private void validateSuperAdminPermission(Integer currentUserRole) {
        if (currentUserRole == null || currentUserRole != 2) {
            log.warn("非超级管理员尝试获取管理员列表，当前角色：{}", currentUserRole);
            throw new BusinessException(ErrorCode.ADMIN_NOT_PERMISSION);
        }
    }

    /**
     * 将查询结果转换为AdminListResponse
     */
    private AdminListResponse convertToAdminListResponse(List<Map<String, Object>> adminMaps, Integer total) {
        List<AdminListResponse.AdminItem> adminItems = new ArrayList<>();

        if (adminMaps != null) {
            for (Map<String, Object> adminMap : adminMaps) {
                AdminListResponse.AdminItem item = convertMapToAdminItem(adminMap);
                adminItems.add(item);
            }
        }

        return new AdminListResponse(total, adminItems);
    }

    /**
     * 将Map转换为AdminItem
     */
    private AdminListResponse.AdminItem convertMapToAdminItem(Map<String, Object> adminMap) {
        AdminListResponse.AdminItem item = new AdminListResponse.AdminItem();

        // 设置ID
        if (adminMap.get("id") != null) {
            item.setId(((Number) adminMap.get("id")).longValue());
        }

        // 设置用户名
        item.setUsername((String) adminMap.get("username"));

        // 设置手机号
        item.setPhone((String) adminMap.get("phone"));

        // 设置邮箱
        item.setEmail((String) adminMap.get("email"));

        // 设置头像
        item.setAvatar((String) adminMap.get("avatar"));

        // 设置状态（转换为字符串）
        Integer status = (Integer) adminMap.get("status");
        item.setStatus(convertStatusToString(status));

        // 设置生日
        if (adminMap.get("birthday") != null) {
            if (adminMap.get("birthday") instanceof java.sql.Date) {
                item.setBirthday(((java.sql.Date) adminMap.get("birthday")).toLocalDate());
            } else if (adminMap.get("birthday") instanceof LocalDate) {
                item.setBirthday((LocalDate) adminMap.get("birthday"));
            }
        }

        // 设置创建时间
        if (adminMap.get("created_time") != null) {
            if (adminMap.get("created_time") instanceof java.sql.Timestamp) {
                item.setCreatedTime(((java.sql.Timestamp) adminMap.get("created_time")).toLocalDateTime());
            } else if (adminMap.get("created_time") instanceof LocalDateTime) {
                item.setCreatedTime((LocalDateTime) adminMap.get("created_time"));
            }
        }

        // 设置更新时间
        if (adminMap.get("updated_time") != null) {
            if (adminMap.get("updated_time") instanceof java.sql.Timestamp) {
                item.setUpdatedTime(((java.sql.Timestamp) adminMap.get("updated_time")).toLocalDateTime());
            } else if (adminMap.get("updated_time") instanceof LocalDateTime) {
                item.setUpdatedTime((LocalDateTime) adminMap.get("updated_time"));
            }
        }

        return item;
    }

    /**
     * 将数据库状态转换为字符串
     */
    private String convertStatusToString(Integer status) {
        if (status == null) {
            return "unknown";
        }

        return status == 1 ? "active" : "disabled";
    }

    /**
     * 验证目标用户是否存在且是管理员
     */
    private User validateTargetAdmin(Long targetAdminId) {
        if (targetAdminId == null) {
            throw new BusinessException(ErrorCode.ADMIN_OPERATION_FAILED, "管理员ID不能为空");
        }

        User targetAdmin = userMapper.selectAdminUserById(targetAdminId);
        if (targetAdmin == null) {
            log.warn("尝试删除不存在的管理员，管理员ID：{}", targetAdminId);
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }

        // 确认是管理员（role=1）或超级管理员（role=2）
        if (targetAdmin.getRole() == 0) {
            log.warn("尝试删除普通用户作为管理员，用户ID：{}", targetAdminId);
            throw new BusinessException(ErrorCode.ADMIN_CANNOT_DELETE_ADMIN, "该用户不是管理员");
        }

        return targetAdmin;
    }

    /**
     * 验证不能删除自己
     */
    private void validateNotSelf(Long currentUserId, Long targetAdminId) {
        if (currentUserId.equals(targetAdminId)) {
            log.warn("管理员尝试删除自己，管理员ID：{}", currentUserId);
            throw new BusinessException(ErrorCode.ADMIN_CANNOT_DELETE_SELF);
        }
    }

    /**
     * 验证不能删除超级管理员
     */
    private void validateNotSuperAdmin(User targetAdmin) {
        if (targetAdmin.getRole() == 2) {
            log.warn("尝试删除超级管理员，管理员ID：{}", targetAdmin.getId());
            throw new BusinessException(ErrorCode.ADMIN_CANNOT_DELETE_SUPER_ADMIN);
        }
    }

    /**
     * 验证至少保留一个管理员（根据业务需求可选）
     */
    private void validateMinimumAdmins() {
        // 如果业务要求至少保留一个管理员，可以在此验证
        int adminCount = userMapper.countAdminUsers();
        if (adminCount <= 1) {
            // 这里假设至少需要保留一个管理员
            log.warn("尝试删除最后一个管理员，当前管理员数量：{}", adminCount);
            throw new BusinessException(ErrorCode.ADMIN_OPERATION_FAILED, "至少需要保留一个管理员");
        }
    }

    /**
     * 验证权限：只有超级管理员可以创建管理员
     */
    private void validatePermission(Integer currentUserRole) {
        if (currentUserRole == null || currentUserRole != 2) {
            log.warn("非超级管理员尝试创建管理员，当前角色：{}", currentUserRole);
            throw new BusinessException(ErrorCode.ADMIN_NOT_PERMISSION);
        }
    }

    /**
     * 验证手机号是否已存在
     */
    private void validatePhoneExists(String phone) {
        // 检查普通用户是否已存在
        int userCount = userMapper.countByPhone(phone);
        if (userCount > 0) {
            // 检查是否已经是管理员
            int adminCount = userMapper.checkAdminExists(phone);
            if (adminCount > 0) {
                log.warn("手机号已经是管理员，手机号：{}", phone);
                throw new BusinessException(ErrorCode.ADMIN_CANNOT_DELETE_ADMIN, "该用户已经是管理员");
            }
        }
    }

    /**
     * 生成用户名：手机号后6位，如果重复则追加随机数
     */
    private String generateUsername(String phone) {
        String baseUsername;
        if (phone.length() >= 6) {
            baseUsername = phone.substring(phone.length() - 6);
        } else {
            baseUsername = phone;
        }

        String username = baseUsername;
        int attempt = 0;
        final int MAX_ATTEMPTS = 5;

        while (attempt < MAX_ATTEMPTS) {
            int count = userMapper.countByUsername(username);
            if (count == 0) {
                return username;
            }

            // 用户名重复，追加4位随机数字
            String randomDigits = generateRandomDigits(4);
            username = baseUsername + randomDigits;
            attempt++;
        }

        // 如果多次尝试仍然冲突，使用8位随机字母数字
        log.warn("用户名生成多次冲突，使用随机字符串生成，手机号：{}", phone);
        return "admin_" + generateRandomAlphanumeric(8);
    }

    /**
     * 生成指定长度的随机数字字符串
     * @param length 字符串长度
     * @return 随机数字字符串
     */
    private String generateRandomDigits(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * NUMBERS.length());
            sb.append(NUMBERS.charAt(index));
        }
        return sb.toString();
    }

    /**
     * 生成指定长度的随机字母数字字符串
     * @param length 字符串长度
     * @return 随机字母数字字符串
     */
    private String generateRandomAlphanumeric(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * ALPHANUMERIC.length());
            sb.append(ALPHANUMERIC.charAt(index));
        }
        return sb.toString();
    }

    /**
     * 构建管理员用户实体
     */
    private User buildAdminUser(AdminCreateRequest request, String username) {
        User user = new User();
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setUsername(username);
        user.setRole(1); // 管理员角色
        user.setStatus(1); // 正常状态
        user.setCreatedTime(LocalDateTime.now());
        user.setUpdatedTime(LocalDateTime.now());

        // 设置默认头像
        user.setAvatar(generateDefaultAvatarUrl());

        return user;
    }

    /**
     * 生成默认头像URL
     */
    private String generateDefaultAvatarUrl() {
        // 这里可以使用一个默认头像URL，或者根据用户ID生成
        return "/uploads/avatar/default_avatar.png"; // 修改为你的默认头像路径
    }

    /**
     * 构建创建管理员响应
     */
    private AdminCreateResponse buildAdminCreateResponse(User user) {
        return new AdminCreateResponse(
                user.getId(),
                user.getUsername(),
                user.getAvatar()
        );
    }
}