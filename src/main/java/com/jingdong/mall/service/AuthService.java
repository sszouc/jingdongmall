// service/AuthService.java
package com.jingdong.mall.service;

import com.jingdong.mall.model.dto.request.UserLoginRequest;
import com.jingdong.mall.model.dto.request.UserRegisterRequest;
import com.jingdong.mall.model.dto.request.ResetPasswordRequest;
import com.jingdong.mall.model.dto.response.UserLoginResponse;
import com.jingdong.mall.model.dto.response.UserRegisterResponse;

/*
* 注意，service层要注意接口和实现分开，同样的注意代码复用
* */
public interface AuthService {
    UserRegisterResponse register(UserRegisterRequest request);
    UserLoginResponse login(UserLoginRequest request);
    void resetPassword(ResetPasswordRequest request); // 新增找回密码方法
}