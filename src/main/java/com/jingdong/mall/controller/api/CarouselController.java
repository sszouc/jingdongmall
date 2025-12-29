package com.jingdong.mall.controller.api;

import com.jingdong.mall.common.exception.BusinessException;
import com.jingdong.mall.common.exception.ErrorCode;
import com.jingdong.mall.common.response.Result;
import com.jingdong.mall.common.utils.JwtUtil;
import com.jingdong.mall.model.dto.request.CarouselAddRequest;
import com.jingdong.mall.model.dto.response.CarouselAddResponse;
import com.jingdong.mall.model.dto.response.CarouselDeleteResponse;
import com.jingdong.mall.model.dto.response.CarouselResponse;
import com.jingdong.mall.service.CarouselService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/product/carousel")
@Tag(name = "管理员/轮播图", description = "轮播图相关接口")
public class CarouselController {

    @Autowired
    private CarouselService carouselService;

    @Autowired
    private JwtUtil jwtUtil;

    @Operation(
            summary = "获取轮播图",
            description = "获取所有启用的轮播图，按排序字段升序排列"
    )
    @GetMapping
    public Result<List<CarouselResponse>> getCarouselList() {
        List<CarouselResponse> carouselList = carouselService.getActiveCarousels();
        return Result.success("轮播图获取成功", carouselList);
    }
    // 新增接口
    @Operation(
            summary = "新增轮播图",
            description = "只有imgUrl不能为NULL，其他都可以，sortOrder默认0，isActive默认1",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = {"管理员/轮播图"}
    )
    @PostMapping
    public Result<CarouselAddResponse> addCarousel(
            @Parameter(description = "JWT认证令牌", required = true, example = "Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody CarouselAddRequest request) {

        // 验证管理员权限
        String token = extractTokenFromHeader(authHeader);
        Integer userRole = jwtUtil.getUserRoleFromToken(token);
        if (userRole == null || (userRole != 1 && userRole != 2)) {
            throw new BusinessException(ErrorCode.ADMIN_NOT_PERMISSION);
        }

        CarouselAddResponse response = carouselService.addCarousel(request);
        return Result.success("轮播图新增成功", response);
    }

    @Operation(
            summary = "删除轮播图",
            description = "删除指定ID的轮播图，仅管理员可操作",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = {"管理员/轮播图"}
    )
    @DeleteMapping("/{id}")
    public Result<CarouselDeleteResponse> deleteCarousel(
            @Parameter(description = "JWT认证令牌", required = true, example = "Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader("Authorization") String authHeader,
            @Parameter(description = "轮播图ID", required = true, example = "1")
            @PathVariable @NotNull(message = "轮播图ID不能为空") Long id) {
        // 验证管理员权限
        String token = extractTokenFromHeader(authHeader);
        Integer userRole = jwtUtil.getUserRoleFromToken(token);
        if (userRole == null || (userRole != 1 && userRole != 2)) {
            throw new BusinessException(ErrorCode.ADMIN_NOT_PERMISSION);
        }

        CarouselDeleteResponse response = carouselService.deleteCarousel(id);
        return Result.success("轮播图删除成功", response);
    }
    // 新增Token提取工具方法
    private String extractTokenFromHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BusinessException(ErrorCode.TOKEN_INVALID_FORMAT);
        }
        return authHeader.substring(7);
    }
}