package com.jingdong.mall.controller.api;

import com.jingdong.mall.common.response.Result;
import com.jingdong.mall.model.dto.response.CategoryTreeResponse;
import com.jingdong.mall.service.ProductCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/products/category")
@Tag(name = "管理员/分类管理", description = "商品分类管理相关接口")
public class CategoryTreeController {

    @Autowired
    private ProductCategoryService productCategoryService;

    @Operation(
            summary = "获取分类树",
            description = "获取所有一级分类（平行展示），parent_id字段保留不删除",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/list")
    public Result<CategoryTreeResponse> getCategoryTree() {
        CategoryTreeResponse response = productCategoryService.getCategoryTree();
        return Result.success("分类树获取成功", response);
    }
}