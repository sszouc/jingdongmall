// controller/api/AddressController.java
package com.jingdong.mall.controller.api;

import com.jingdong.mall.common.exception.BusinessException;
import com.jingdong.mall.common.exception.ErrorCode;
import com.jingdong.mall.common.response.Result;
import com.jingdong.mall.common.utils.JwtUtil;
import com.jingdong.mall.model.dto.response.AddressResponse;
import com.jingdong.mall.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/address")
@Tag(name = "地址管理", description = "用户收货地址相关接口")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @Autowired
    private JwtUtil jwtUtil;

    @Operation(
            summary = "获取用户地址列表",
            description = "获取当前登录用户的所有收货地址",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/list")
    public Result<List<AddressResponse>> getAddressList(
            @Parameter(description = "JWT认证令牌", required = true, example = "Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader("Authorization") String authHeader) {
        //authHeader是一个带提示信息的Token，示例：
        //Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNzY0NzUzMTEyLCJleHAiOjE3NjQ3NTU3MDR9.L4D9sfiJXWK87eQR5k_yt_3cGgl7snOZ5NgSWOkD4-lW5KWVdM72-PzlduPmZZylXT_csrz3LjgeXquDZh-SuQ
        // 1. 验证和解析Token
        log.info("authHeader={}", authHeader);
        String token = extractTokenFromHeader(authHeader);
        String userIdStr = jwtUtil.getUserIdFromToken(token);

        // 2. 获取用户地址
        Long userId = Long.parseLong(userIdStr);
        List<AddressResponse> addresses = addressService.getUserAddresses(userId);

        // 3. 返回成功响应
        return Result.success(addresses);
    }

    private String extractTokenFromHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BusinessException(ErrorCode.TOKEN_INVALID_FORMAT);
        }
        return authHeader.substring(7); // 去掉"Bearer "前缀
    }
}