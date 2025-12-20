package com.jingdong.mall.service.impl;

import com.jingdong.mall.common.exception.BusinessException;
import com.jingdong.mall.common.exception.ErrorCode;
import com.jingdong.mall.mapper.UserMapper;
import com.jingdong.mall.model.dto.request.AdminUserListRequest;
import com.jingdong.mall.model.dto.request.UserStatusUpdateRequest;
import com.jingdong.mall.model.dto.response.AdminUserListResponse;
import com.jingdong.mall.model.entity.User;
import com.jingdong.mall.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public void deleteUser(Long currentUserId, Integer currentUserRole, Long targetUserId) {
        try {
            log.info("管理员删除用户: currentUserId={}, currentUserRole={}, targetUserId={}",
                    currentUserId, currentUserRole, targetUserId);

            // 1. 验证当前用户是否为管理员（role不为0）
            if (currentUserRole == null || currentUserRole == 0) {
                log.warn("非管理员尝试删除用户: currentUserId={}, role={}", currentUserId, currentUserRole);
                throw new BusinessException(ErrorCode.ADMIN_NOT_PERMISSION);
            }

            // 2. 验证目标用户是否存在
            User targetUser = userMapper.selectById(targetUserId);
            if (targetUser == null) {
                log.warn("删除用户失败: 用户不存在, targetUserId={}", targetUserId);
                throw new BusinessException(ErrorCode.USER_NOT_EXIST);
            }

            // 3. 验证目标用户角色是否为普通用户（role=0）
            if (targetUser.getRole() != 0) {
                log.warn("删除用户失败: 不能删除管理员用户, targetUserId={}, targetUserRole={}",
                        targetUserId, targetUser.getRole());
                throw new BusinessException(ErrorCode.ADMIN_CANNOT_DELETE_ADMIN);
            }

            // 4. 防止管理员删除自己
            if (targetUserId.equals(currentUserId)) {
                log.warn("管理员尝试删除自己: currentUserId={}", currentUserId);
                throw new BusinessException(ErrorCode.ADMIN_CANNOT_DELETE_SELF);
            }

            // 5. 执行删除操作
            int result = userMapper.deleteById(targetUserId);
            if (result <= 0) {
                log.error("删除用户失败: 数据库删除操作返回0, targetUserId={}", targetUserId);
                throw new BusinessException(ErrorCode.ADMIN_OPERATION_FAILED);
            }

            log.info("用户删除成功: targetUserId={}", targetUserId);

        } catch (BusinessException e) {
            // 业务异常直接抛出
            log.warn("删除用户业务异常: currentUserId={}, targetUserId={}, message={}",
                    currentUserId, targetUserId, e.getMessage());
            throw e;
        } catch (Exception e) {
            // 系统异常包装后抛出
            log.error("删除用户系统异常: currentUserId={}, targetUserId={}", currentUserId, targetUserId, e);
            throw new BusinessException(ErrorCode.ADMIN_OPERATION_FAILED);
        }
    }

    @Override
    public AdminUserListResponse getUserList(Long currentUserId, Integer currentUserRole, AdminUserListRequest request) {
        try {
            log.info("管理员查询用户列表: currentUserId={}, currentUserRole={}, request={}",
                    currentUserId, currentUserRole, request);

            // 1. 验证当前用户是否为管理员（role不为0）
            if (currentUserRole == null || currentUserRole == 0) {
                log.warn("非管理员尝试查询用户列表: currentUserId={}, role={}", currentUserId, currentUserRole);
                throw new BusinessException(ErrorCode.ADMIN_NOT_PERMISSION);
            }

            // 2. 验证分页参数
            if (request.getPage() <= 0 || request.getPageSize() <= 0) {
                log.warn("分页参数错误: page={}, pageSize={}", request.getPage(), request.getPageSize());
                throw new BusinessException(ErrorCode.ADMIN_QUERY_FAILED, "分页参数错误");
            }

            // 3. 查询数据
            List<User> userList = userMapper.selectUserList(request);
            Long total = userMapper.countUserList(request);

            // 4. 转换数据
            List<AdminUserListResponse.UserItem> userItems = userList.stream()
                    .map(this::convertToUserItem)
                    .collect(Collectors.toList());

            // 5. 构建响应
            AdminUserListResponse response = new AdminUserListResponse();
            response.setTotal(total);
            response.setPage(request.getPage());
            response.setPageSize(request.getPageSize());
            response.setList(userItems);

            return response;

        } catch (BusinessException e) {
            // 业务异常直接抛出
            log.warn("查询用户列表业务异常: currentUserId={}, message={}", currentUserId, e.getMessage());
            throw e;
        } catch (Exception e) {
            // 系统异常包装后抛出
            log.error("查询用户列表系统异常: currentUserId={}", currentUserId, e);
            throw new BusinessException(ErrorCode.ADMIN_QUERY_FAILED);
        }
    }

    @Override
    @Transactional
    public void updateUserStatus(Long currentUserId, Integer currentUserRole,
                                 Long targetUserId, UserStatusUpdateRequest request) {
        try {
            log.info("管理员更新用户状态: adminId={}, targetUserId={}, action={}, endTime={}",
                    currentUserId, targetUserId, request.getAction(), request.getEndTime());

            // 1. 验证当前用户权限
            if (currentUserRole != User.Role.ADMIN && currentUserRole != User.Role.SUPER_ADMIN) {
                throw new BusinessException(ErrorCode.NOT_PERMISSION);
            }

            // 2. 获取目标用户信息
            User targetUser = userMapper.selectStatusById(targetUserId);
            if (targetUser == null) {
                throw new BusinessException(ErrorCode.USER_NOT_EXIST);
            }

            // 3. 验证目标用户角色（只能操作普通用户）
            if (targetUser.getRole() != User.Role.USER) {
                throw new BusinessException(ErrorCode.NOT_PERMISSION, "只能操作普通用户");
            }

            // 4. 根据操作类型处理
            if (request.getAction() == 1) {
                // 封禁操作
                handleBanUser(targetUser, request);
            } else if (request.getAction() == 0) {
                // 解禁操作
                handleUnbanUser(targetUser);
            } else {
                throw new BusinessException(ErrorCode.INFO_UPDATE_FAIL, "操作类型不合法");
            }

            log.info("用户状态更新成功: userId={}, action={}", targetUserId, request.getAction());
        } catch (BusinessException e) {
            log.error("更新用户状态业务异常: adminId={}, targetUserId={}, error={}",
                    currentUserId, targetUserId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("更新用户状态系统异常: adminId={}, targetUserId={}, error={}",
                    currentUserId, targetUserId, e.getMessage());
            throw new BusinessException(ErrorCode.INFO_UPDATE_FAIL, "更新用户状态时发生系统错误");
        }
    }

    /**
     * 处理封禁用户逻辑
     */
    private void handleBanUser(User targetUser, UserStatusUpdateRequest request) {
        // 检查用户是否已经封禁
        if (targetUser.getStatus() == User.Status.DISABLED) {
            throw new BusinessException(ErrorCode.INFO_UPDATE_FAIL, "用户已被封禁");
        }

        // 验证封禁时长
        if (request.getEndTime() == null || request.getEndTime() <= 0) {
            throw new BusinessException(ErrorCode.INFO_UPDATE_FAIL, "封禁时长必须大于0小时");
        }

        // 计算封禁时间
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = now.plusHours(request.getEndTime());

        // 更新用户状态和封禁时间
        int result = userMapper.updateUserStatusAndBanTime(
                targetUser.getId(),
                User.Status.DISABLED,  // 状态置为禁用
                now,                   // 封禁开始时间
                endTime                // 封禁结束时间
        );

        if (result <= 0) {
            throw new BusinessException(ErrorCode.INFO_UPDATE_FAIL, "封禁用户失败");
        }
    }

    /**
     * 处理解禁用户逻辑
     */
    private void handleUnbanUser(User targetUser) {
        // 检查用户是否已经被封禁
        if (targetUser.getStatus() == User.Status.ENABLED) {
            throw new BusinessException(ErrorCode.INFO_UPDATE_FAIL, "用户未被封禁");
        }

        // 更新用户状态，清除封禁时间
        int result = userMapper.updateUserStatusAndBanTime(
                targetUser.getId(),
                User.Status.ENABLED,  // 状态置为启用
                null,                 // 清除封禁开始时间
                null                  // 清除封禁结束时间
        );

        if (result <= 0) {
            throw new BusinessException(ErrorCode.INFO_UPDATE_FAIL, "解禁用户失败");
        }
    }

    // 将User实体转换为AdminUserListResponse.UserItem DTO
    private AdminUserListResponse.UserItem convertToUserItem(User user) {
        AdminUserListResponse.UserItem item = new AdminUserListResponse.UserItem();
        item.setId(user.getId());
        item.setUsername(user.getUsername());
        item.setPhone(user.getPhone());
        item.setEmail(user.getEmail());
        item.setAvatar(user.getAvatar());
        // 转换状态：1 -> "active", 0 -> "disabled"
        if (user.getStatus() != null) {
            item.setStatus(user.getStatus() == 1 ? "active" : "disabled");
        }
        item.setBirthday(user.getBirthday());
        item.setCreatedTime(user.getCreatedTime());
        item.setUpdatedTime(user.getUpdatedTime());
        return item;
    }
}