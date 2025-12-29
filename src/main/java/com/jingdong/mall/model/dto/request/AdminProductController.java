package com.jingdong.mall.controller.api;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理员商品管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/products")
@Tag(name = "管理员/商品管理", description = "商品管理相关接口")
public class AdminProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 新增商品接口
     */
    @Operation(
            summary = "新增商品",
            description = "创建新的商品，名称不能重复，分类ID可选",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping
    public Result<ProductAddResponse> addProduct(
            @Parameter(description = "JWT认证令牌", required = true, example = "Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody ProductAddRequest request) {

        // 验证管理员权限（通过Token角色判断）
        String token = extractTokenFromHeader(authHeader);
        Integer userRole = jwtUtil.getUserRoleFromToken(token);
        if (userRole == null || (userRole != 1 && userRole != 2)) {
            throw new BusinessException(ErrorCode.ADMIN_NOT_PERMISSION);
        }

        ProductAddResponse response = productService.addProduct(request);
        return Result.success("商品新增成功", response);
    }

    /**
     * 从请求头提取Token
     */
    private String extractTokenFromHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BusinessException(ErrorCode.TOKEN_INVALID_FORMAT);
        }
        return authHeader.substring(7);
    }
}