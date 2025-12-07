package com.jingdong.mall.controller.api;

import com.jingdong.mall.common.exception.BusinessException;
import com.jingdong.mall.common.response.Result;
import com.jingdong.mall.model.dto.response.ProductDetailResponse;
import com.jingdong.mall.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/products")
@Tag(name = "商品管理", description = "商品相关接口")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Operation(
            summary = "获取商品详情",
            description = "获取商品的详细信息，包括基本信息、规格、SKU列表和参数"
    )
    @GetMapping("/{id}")
    public Result<ProductDetailResponse> getProductDetail(
            @Parameter(description = "商品主ID", required = true, example = "123161")
            @PathVariable Integer id) {

        try {
            log.info("获取商品详情: id={}", id);

            ProductDetailResponse productDetail = productService.getProductDetail(id);

            return Result.success("获取商品详情成功", productDetail);
        } catch (BusinessException e) {
            log.warn("获取商品详情业务异常: id={}, message={}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("获取商品详情系统异常: id={}", id, e);
            throw new BusinessException("获取商品详情失败，请稍后重试");
        }
    }
}