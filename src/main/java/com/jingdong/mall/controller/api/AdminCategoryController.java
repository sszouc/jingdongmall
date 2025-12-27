package com.jingdong.mall.controller.api;

import com.jingdong.mall.common.response.Result;
import com.jingdong.mall.model.dto.request.ProductCategoryAddRequest;
import com.jingdong.mall.model.dto.response.ProductCategoryAddResponse;
import com.jingdong.mall.service.ProductCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/admin/categories")
@Tag(name = "管理员/分类管理", description = "商品分类管理相关接口")
public class AdminCategoryController {

    @Autowired
    private ProductCategoryService productCategoryService;

    @Operation(
            summary = "新增分类",
            description = "创建新的商品分类，parentId默认0（一级分类），自动启用"
    )
    @PostMapping
    public Result<ProductCategoryAddResponse> addCategory(
            @Parameter(description = "新增分类请求参数", required = true)
            @Valid @RequestBody ProductCategoryAddRequest request) {
        log.info("管理员新增分类：name={}, subTitle={}", request.getName(), request.getSubTitle());
        ProductCategoryAddResponse response = productCategoryService.addCategory(request);
        return Result.success("分类创建成功", response);
    }
}