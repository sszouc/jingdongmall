// service/impl/AuthServiceImpl.java
package com.jingdong.mall.service.impl;

import com.jingdong.mall.common.exception.BusinessException;
import com.jingdong.mall.common.exception.ErrorCode;
import com.jingdong.mall.common.utils.JwtUtil;
import com.jingdong.mall.mapper.UserMapper;
import com.jingdong.mall.model.entity.User;
import com.jingdong.mall.model.dto.request.UserRegisterRequest;
import com.jingdong.mall.model.dto.response.UserRegisterResponse;
import com.jingdong.mall.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

/*
* 实现层
* */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public UserRegisterResponse register(UserRegisterRequest request) {
        // 1. 参数校验
        validateRegisterParams(request);

        // 2. 构建用户实体
        User user = buildUserEntity(request);

        // 3. 保存用户
        int result = userMapper.insert(user);
        if (result <= 0) {
            throw new BusinessException("注册失败，请稍后重试");
        }

        // 4. 生成token和响应
        return buildRegisterResponse(user);
    }

    private void validateRegisterParams(UserRegisterRequest request) {

        if (!Pattern.compile("^1[3-9]\\d{9}$").matcher(request.getPhone()).matches()) {
            throw new BusinessException(ErrorCode.PHONE_FORMAT_ERROR);
        }

        // 检查手机号是否已注册
        if (StringUtils.hasText(request.getPhone()) && userMapper.countByPhone(request.getPhone()) > 0) {
            throw new BusinessException(ErrorCode.PHONE_EXISTED);
        }

        // 验证码校验
        if (StringUtils.hasText(request.getPhone()) && !validateSmsCode(request.getPhone(), request.getCode())) {
            throw new BusinessException(ErrorCode.VERIFY_CODE_EXPIRED);
        }

    }

    private User buildUserEntity(UserRegisterRequest request) {
        User user = new User();
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // 生成默认昵称
        String timestamp = String.valueOf(System.currentTimeMillis());
        String userName = "用户" + timestamp;
        user.setUsername(userName);
        //默认是普通用户
        user.setRole(0);

        user.setAvatar("https://example.com/default-avatar.png");
        user.setStatus(1); // 正常状态
        user.setCreatedTime(LocalDateTime.now());
        user.setUpdatedTime(LocalDateTime.now());

        return user;
    }

    private UserRegisterResponse buildRegisterResponse(User user) {
        UserRegisterResponse response = new UserRegisterResponse();

        // 生成JWT token
        String token = jwtUtil.generateToken(String.valueOf(user.getId()));
        response.setToken(token);

        // 构建用户信息
        UserRegisterResponse.UserInfo userInfo = new UserRegisterResponse.UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUserName(user.getUsername());
        userInfo.setAvatar(user.getAvatar());
        response.setUserInfo(userInfo);

        return response;
    }

    private boolean validateSmsCode(String phone, String code) {
        // 这里实现验证码验证逻辑
        // 简化实现，实际项目中需要完整的验证码服务
        return "123456".equals(code); // 测试用，实际应该从Redis获取验证码
    }
}