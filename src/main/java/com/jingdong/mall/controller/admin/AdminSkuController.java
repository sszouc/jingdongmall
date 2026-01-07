// src/main/java/com/jingdong/mall/controller/admin/AdminSkuController.java
package com.jingdong.mall.controller.admin;

import com.jingdong.mall.common.exception.BusinessException;
import com.jingdong.mall.common.exception.ErrorCode;
import com.jingdong.mall.common.response.Result;
import com.jingdong.mall.common.utils.JwtUtil;
import com.jingdong.mall.model.dto.request.SkuAddRequest;
import com.jingdong.mall.model.dto.request.SkuUpdateRequest;
import com.jingdong.mall.model.dto.response.SkuAddResponse;
import com.jingdong.mall.service.ProductSkuService;
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
@RequestMapping("/api/admin/sku")
@Tag(name = "管理员/SKU管理", description = "管理员SKU管理相关接口")
public class AdminSkuController {

    @Autowired
    private ProductSkuService productSkuService;

    @Autowired
    private JwtUtil jwtUtil;

    @Operation(
            summary = "新增SKU",
            description = "为指定产品创建新的SKU（具体规格）",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping
    public Result<SkuAddResponse> addSku(
            @Parameter(description = "JWT认证令牌", required = true)
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody SkuAddRequest request) {

        // 1. 提取并验证token
        String token = extractTokenFromHeader(authHeader);

        // 2. 验证管理员权限
        validateAdminRole(token);

        // 3. 调用服务层新增SKU
        SkuAddResponse response = productSkuService.addSku(request);

        log.info("管理员新增SKU成功，SKU ID：{}，产品ID：{}", response.getId(), request.getProductId());

        return Result.success("SKU创建成功", response);
    }

    @Operation(
            summary = "更新SKU",
            description = "更新指定ID的SKU信息",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/{id}")
    public Result<Void> updateSku(
            @Parameter(description = "JWT认证令牌", required = true)
            @RequestHeader("Authorization") String authHeader,
            @Parameter(description = "SKU ID", required = true, example = "1")
            @PathVariable("id") Integer skuId,
            @Valid @RequestBody SkuUpdateRequest request) {

        // 1. 提取并验证token
        String token = extractTokenFromHeader(authHeader);

        // 2. 验证管理员权限
        validateAdminRole(token);

        // 3. 调用服务层更新SKU
        productSkuService.updateSku(skuId, request);

        log.info("管理员更新SKU成功，SKU ID：{}", skuId);

        return Result.success("SKU更新成功", null);
    }

    @Operation(
            summary = "删除SKU",
            description = "删除指定ID的SKU",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/{id}")
    public Result<Void> deleteSku(
            @Parameter(description = "JWT认证令牌", required = true)
            @RequestHeader("Authorization") String authHeader,
            @Parameter(description = "SKU ID", required = true, example = "1")
            @PathVariable("id") Integer skuId) {

        // 1. 提取并验证token
        String token = extractTokenFromHeader(authHeader);

        // 2. 验证管理员权限
        validateAdminRole(token);

        // 3. 调用服务层删除SKU
        productSkuService.deleteSku(skuId);

        log.info("管理员删除SKU成功，SKU ID：{}", skuId);

        return Result.success("SKU删除成功", null);
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

    /**
     * 验证管理员权限
     */
    private void validateAdminRole(String token) {
        Integer userRole = jwtUtil.getUserRoleFromToken(token);
        if (userRole == null || (userRole != 1 && userRole != 2)) {
            log.warn("非管理员尝试操作SKU，用户角色：{}", userRole);
            throw new BusinessException(ErrorCode.ADMIN_NOT_PERMISSION);
        }
    }
}