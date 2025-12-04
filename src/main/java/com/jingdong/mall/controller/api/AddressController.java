package com.jingdong.mall.controller.api;

import com.jingdong.mall.common.exception.BusinessException;
import com.jingdong.mall.common.exception.ErrorCode;
import com.jingdong.mall.common.response.Result;
import com.jingdong.mall.common.utils.JwtUtil;
import com.jingdong.mall.model.dto.request.AddressAddRequest;
import com.jingdong.mall.model.dto.request.AddressUpdateRequest;
import com.jingdong.mall.model.dto.response.AddressResponse;
import com.jingdong.mall.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

    // 原有获取地址列表接口（保持不变，已对齐OpenAPI规范）
    @Operation(
            summary = "获取用户地址列表",
            description = "获取当前登录用户的所有收货地址",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/list")
    public Result<List<AddressResponse>> getAddressList(
            @Parameter(description = "JWT认证令牌", required = true, example = "Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader("Authorization") String authHeader) {
        String token = extractTokenFromHeader(authHeader);
        String userIdStr = jwtUtil.getUserIdFromToken(token);
        Long userId = Long.parseLong(userIdStr);
        List<AddressResponse> addresses = addressService.getUserAddresses(userId);
        return Result.success(addresses);
    }

    // 新增接口：添加地址
    @Operation(
            summary = "新增收货地址",
            description = "为当前登录用户新增收货地址",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/add")
    public Result<AddressResponse> addAddress(
            @Parameter(description = "JWT认证令牌", required = true)
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody AddressAddRequest request) {
        String token = extractTokenFromHeader(authHeader);
        String userIdStr = jwtUtil.getUserIdFromToken(token);
        Long userId = Long.parseLong(userIdStr);
        AddressResponse response = addressService.addAddress(userId, request);
        return Result.success("新增地址成功", response);
    }

    // 新增接口：修改地址
    @Operation(
            summary = "修改收货地址",
            description = "修改当前登录用户的指定收货地址",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/update")
    public Result<AddressResponse> updateAddress(
            @Parameter(description = "JWT认证令牌", required = true)
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody AddressUpdateRequest request) {
        String token = extractTokenFromHeader(authHeader);
        String userIdStr = jwtUtil.getUserIdFromToken(token);
        Long userId = Long.parseLong(userIdStr);
        AddressResponse response = addressService.updateAddress(userId, request);
        return Result.success("修改地址成功", response);
    }

    // 新增接口：删除地址
    @Operation(
            summary = "删除收货地址",
            description = "删除当前登录用户的指定收货地址",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/delete/{addressId}")
    public Result<String> deleteAddress(
            @Parameter(description = "JWT认证令牌", required = true)
            @RequestHeader("Authorization") String authHeader,
            @Parameter(description = "地址ID", required = true, example = "1")
            @PathVariable Integer addressId) {
        String token = extractTokenFromHeader(authHeader);
        String userIdStr = jwtUtil.getUserIdFromToken(token);
        Long userId = Long.parseLong(userIdStr);
        boolean success = addressService.deleteAddress(userId, addressId);
        return success ? Result.success("删除地址成功") : Result.error("删除地址失败");
    }

    // 原有工具方法保持不变
    private String extractTokenFromHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BusinessException(ErrorCode.TOKEN_INVALID_FORMAT);
        }
        return authHeader.substring(7);
    }
}