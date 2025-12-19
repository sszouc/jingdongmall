package com.jingdong.mall.service.impl;

import com.jingdong.mall.common.exception.BusinessException;
import com.jingdong.mall.common.exception.ErrorCode;
import com.jingdong.mall.mapper.UserMapper;
import com.jingdong.mall.model.entity.User;
import com.jingdong.mall.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                throw new BusinessException(ErrorCode.NOT_PERMISSION, "需要管理员权限");
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
                throw new BusinessException(ErrorCode.NOT_PERMISSION, "只能删除普通用户");
            }

            // 4. 防止管理员删除自己
            if (targetUserId.equals(currentUserId)) {
                log.warn("管理员尝试删除自己: currentUserId={}", currentUserId);
                throw new BusinessException(ErrorCode.NOT_PERMISSION, "不能删除自己");
            }

            // 5. 执行删除操作
            int result = userMapper.deleteById(targetUserId);
            if (result <= 0) {
                log.error("删除用户失败: 数据库删除操作返回0, targetUserId={}", targetUserId);
                throw new BusinessException("删除用户失败");
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
            throw new BusinessException("删除用户失败，请稍后重试");
        }
    }
}