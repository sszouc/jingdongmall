package com.jingdong.mall.controller.admin;

import com.jingdong.mall.common.exception.BusinessException;
import com.jingdong.mall.common.exception.ErrorCode;
import com.jingdong.mall.common.response.Result;
import com.jingdong.mall.common.utils.JwtUtil;
import com.jingdong.mall.model.dto.request.ProductAddRequest;
import com.jingdong.mall.model.dto.response.ProductAddResponse;
import com.jingdong.mall.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/admin/products")
@Tag(name = "管理员/商品管理", description = "管理员商品管理相关接口")
public class AdminProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private JwtUtil jwtUtil;

    @Operation(
            summary = "新增商品",
            description = "创建新的商品，注意商品名称不能重复",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping
    public Result<ProductAddResponse> addProduct(
            @Parameter(description = "JWT认证令牌", required = true)
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody ProductAddRequest request) {

        // 1. 提取并验证token
        String token = extractTokenFromHeader(authHeader);

        // 2. 验证管理员权限
        Integer userRole = jwtUtil.getUserRoleFromToken(token);
        if (userRole == null || (userRole != 1 && userRole != 2)) {
            log.warn("非管理员尝试新增商品，用户角色：{}", userRole);
            throw new BusinessException(ErrorCode.ADMIN_NOT_PERMISSION);
        }

        // 3. 调用服务层新增商品
        ProductAddResponse response = productService.addProduct(request);

        log.info("管理员新增商品成功，商品ID：{}，商品名称：{}", response.getId(), request.getName());

        return Result.success("商品新增成功", response);
    }

    /**
     * 从请求头中提取token
     */
    private String extractTokenFromHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BusinessException(ErrorCode.TOKEN_INVALID_FORMAT);
        }
        return authHeader.substring(7);
    }
}