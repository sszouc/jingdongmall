package com.jingdong.mall.controller.api;

import com.jingdong.mall.common.exception.BusinessException;
import com.jingdong.mall.common.response.Result;
import com.jingdong.mall.model.dto.request.ProductListRequest;
import com.jingdong.mall.model.dto.response.ProductDetailResponse;
import com.jingdong.mall.model.dto.response.ProductListResponse;
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

    @Operation(
            summary = "获取商品列表",
            description = "获取商品列表，支持分页、关键词搜索、分类筛选、价格区间和多种排序方式。注意这里的id是商品id，不是每个sku的id",
            parameters = {
                    @Parameter(name = "keyword", description = "关键字", example = "小新"),
                    @Parameter(name = "categoryId", description = "分类号", example = "2"),
                    @Parameter(name = "page", description = "页码，默认为1", example = "1"),
                    @Parameter(name = "pageSize", description = "每页数量，默认为10", example = "10"),
                    @Parameter(name = "sort", description = "排序方式：price_asc(价格升序), price_desc(价格降序), created_desc(最新创建), sales_desc(销量降序)", example = "price_asc"),
                    @Parameter(name = "minPrice", description = "最低价格。注意这里和数据库保持一致，小数后最多两位，小数前最多十位", example = "1"),
                    @Parameter(name = "maxPrice", description = "最高价格", example = "1999.99")
            }
    )
    @GetMapping("")
    public Result<ProductListResponse> getProductList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(required = false, defaultValue = "created_desc") String sort,
            @RequestParam(required = false) java.math.BigDecimal minPrice,
            @RequestParam(required = false) java.math.BigDecimal maxPrice) {

        try {
            log.info("获取商品列表, 参数: keyword={}, categoryId={}, page={}, pageSize={}, sort={}, minPrice={}, maxPrice={}",
                    keyword, categoryId, page, pageSize, sort, minPrice, maxPrice);

            // 构建请求参数
            ProductListRequest request = new ProductListRequest();
            request.setKeyword(keyword);
            request.setCategoryId(categoryId);
            request.setPage(page);
            request.setPageSize(pageSize);
            request.setSort(sort);
            request.setMinPrice(minPrice);
            request.setMaxPrice(maxPrice);

            ProductListResponse productList = productService.getProductList(request);

            return Result.success("获取商品列表成功", productList);
        } catch (BusinessException e) {
            log.warn("获取商品列表业务异常: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("获取商品列表系统异常", e);
            throw new BusinessException("获取商品列表失败，请稍后重试");
        }
    }
}