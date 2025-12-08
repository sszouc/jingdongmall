// src/main/java/com/jingdong/mall/service/impl/ShoppingCartServiceImpl.java
package com.jingdong.mall.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jingdong.mall.common.exception.BusinessException;
import com.jingdong.mall.common.exception.ErrorCode;
import com.jingdong.mall.mapper.ProductMapper;
import com.jingdong.mall.mapper.ProductSkuMapper;
import com.jingdong.mall.mapper.ShoppingCartMapper;
import com.jingdong.mall.model.dto.response.CartItemResponse;
import com.jingdong.mall.model.dto.response.CartListResponse;
import com.jingdong.mall.model.entity.Product;
import com.jingdong.mall.model.entity.ProductSku;
import com.jingdong.mall.model.entity.ShoppingCart;
import com.jingdong.mall.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private ProductSkuMapper productSkuMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public CartListResponse getUserCartList(Long userId) {
        // 1. 参数校验
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }

        // 2. 查询用户购物车数据
        List<ShoppingCart> cartList = shoppingCartMapper.selectByUserId(userId);
        if (cartList.isEmpty()) {
            return new CartListResponse(new ArrayList<>(), 0, BigDecimal.ZERO);
        }

        // 3. 转换为响应数据
        List<CartItemResponse> cartItemResponses = convertToCartItemResponses(cartList);

        // 4. 计算总数量和总金额
        Integer totalCount = cartItemResponses.stream()
                .mapToInt(CartItemResponse::getCount)
                .sum();

        BigDecimal totalPrice = cartItemResponses.stream()
                .filter(CartItemResponse::getSelected)
                .map(item -> item.getPrice().multiply(new BigDecimal(item.getCount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartListResponse(cartItemResponses, totalCount, totalPrice);
    }

    /**
     * 转换购物车实体列表为响应DTO列表
     */
    private List<CartItemResponse> convertToCartItemResponses(List<ShoppingCart> cartList) {
        return cartList.stream()
                .map(this::convertToCartItemResponse)
                .collect(Collectors.toList());
    }

    /**
     * 转换单个购物车实体为响应DTO
     */
    private CartItemResponse convertToCartItemResponse(ShoppingCart cart) {
        CartItemResponse response = new CartItemResponse();
        response.setId(cart.getId().intValue());

        // 修复：用skuId查单个SKU
        ProductSku sku = productSkuMapper.selectBySkuId(cart.getSkuId());
        if (sku == null || sku.getIsActive() != 1) {
            throw new BusinessException("商品SKU不存在或已下架");
        }

        Product product = productMapper.selectById(sku.getProductId());
        if (product == null || product.getIsActive() != 1) {
            throw new BusinessException("商品不存在或已下架");
        }

        // 封装响应数据
        response.setProductId(product.getId());
        response.setSkuId(sku.getId());
        response.setName(product.getName());
        response.setImgUrl(extractFirstImage(product.getMainImages()));
        response.setSpecs(buildSkuSpecs(sku));
        response.setPrice(sku.getPrice());
        response.setCount(cart.getQuantity());
        response.setStock(sku.getStock());
        response.setSelected(cart.getSelected());

        return response;
    }

    /**
     * 从商品主图JSON中提取第一张图片
     */
    private String extractFirstImage(String mainImagesJson) {
        if (!StringUtils.hasText(mainImagesJson)) {
            return "https://example.com/default-product.jpg";
        }
        try {
            List<String> images = objectMapper.readValue(mainImagesJson, new TypeReference<List<String>>() {});
            return images != null && !images.isEmpty() ? images.get(0) : "https://example.com/default-product.jpg";
        } catch (Exception e) {
            log.warn("解析商品主图失败", e);
            return "https://example.com/default-product.jpg";
        }
    }

    /**
     * 构建SKU规格映射（适配响应规范）
     */
    private Map<String, String> buildSkuSpecs(ProductSku sku) {
        Map<String, String> specs = new HashMap<>();
        if (StringUtils.hasText(sku.getOs())) {
            specs.put("操作系统", sku.getOs());
        }
        if (StringUtils.hasText(sku.getCpu())) {
            specs.put("处理器", sku.getCpu());
        }
        if (StringUtils.hasText(sku.getRam())) {
            specs.put("内存容量", sku.getRam());
        }
        if (StringUtils.hasText(sku.getStorage())) {
            specs.put("存储容量", sku.getStorage());
        }
        if (StringUtils.hasText(sku.getGpu())) {
            specs.put("显卡", sku.getGpu());
        }
        return specs;
    }
}