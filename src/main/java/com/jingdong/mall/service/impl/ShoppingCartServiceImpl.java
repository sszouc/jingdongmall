// src/main/java/com/jingdong/mall/service/impl/ShoppingCartServiceImpl.java
package com.jingdong.mall.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jingdong.mall.common.exception.BusinessException;
import com.jingdong.mall.common.exception.ErrorCode;
import com.jingdong.mall.mapper.ProductMapper;
import com.jingdong.mall.mapper.ProductSkuMapper;
import com.jingdong.mall.mapper.ShoppingCartMapper;
import com.jingdong.mall.model.dto.request.CartAddRequest;
import com.jingdong.mall.model.dto.request.CartBatchSelectRequest;
import com.jingdong.mall.model.dto.request.CartDeleteRequest;
import com.jingdong.mall.model.dto.request.CartUpdateRequest;
import com.jingdong.mall.model.dto.response.*;
import com.jingdong.mall.model.entity.Product;
import com.jingdong.mall.model.entity.ProductSku;
import com.jingdong.mall.model.entity.ShoppingCart;
import com.jingdong.mall.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
            throw new BusinessException(ErrorCode.SKU_NOT_EXIST);
        }

        Product product = productMapper.selectById(sku.getProductId());
        if (product == null || product.getIsActive() != 1) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_EXIST);
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

    public CartItemResponse addCart(Long userId, CartAddRequest request) {
        // 1. 参数校验
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }
        // 2. 验证商品和SKU有效性
        ProductSku sku = productSkuMapper.selectBySkuId(request.getSkuId());
        if (sku == null || sku.getIsActive() != 1) {
            throw new BusinessException(ErrorCode.SKU_NOT_EXIST);
        }

        Product product = productMapper.selectById(request.getProductId());
        if (product == null || product.getIsActive() != 1) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_EXIST);
        }

        // 3. 验证库存
        if (sku.getStock() < request.getCount()) {
            throw new BusinessException(ErrorCode.PRODUCT_STOCK_NOT_ENOUGH);
        }

        // 4. 查询用户是否已添加该SKU
        List<ShoppingCart> existingCart = shoppingCartMapper.selectByUserId(userId);
        ShoppingCart targetCart = existingCart.stream()
                .filter(cart -> cart.getSkuId().equals(request.getSkuId()))
                .findFirst()
                .orElse(null);

        // 5. 已存在则累加数量，不存在则新增
        if (targetCart != null) {
            int newCount = targetCart.getQuantity() + request.getCount();
            if (newCount > sku.getStock()) {
                throw new BusinessException(ErrorCode.PRODUCT_STOCK_NOT_ENOUGH);
            }
            targetCart.setQuantity(newCount);
            targetCart.setUpdatedTime(LocalDateTime.now());
            shoppingCartMapper.update(targetCart); // 需新增ShoppingCartMapper的update方法
        } else {
            targetCart = new ShoppingCart();
            targetCart.setUserId(userId);
            targetCart.setSkuId(request.getSkuId());
            targetCart.setQuantity(request.getCount());
            targetCart.setSelected(true); // 默认选中
            targetCart.setCreatedTime(LocalDateTime.now());
            targetCart.setUpdatedTime(LocalDateTime.now());
            shoppingCartMapper.insert(targetCart); // 需新增ShoppingCartMapper的insert方法
        }

        // 6. 转换为响应DTO并返回
        return convertToCartItemResponse(targetCart);
    }

    /**
     * 更新购物车商品（数量/选中状态）
     */
    @Override
    @Transactional
    public CartItemResponse updateCart(Long userId, CartUpdateRequest request) {
        // 1. 参数校验
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }
        if (!request.hasUpdatableField()) {
            throw new BusinessException("请提供需要更新的字段（数量或选中状态）");
        }

        // 2. 校验购物车条目是否存在且归属当前用户
        ShoppingCart cartItem = shoppingCartMapper.selectByIdAndUserId(request.getId(), userId);
        if (cartItem == null) {
            throw new BusinessException(ErrorCode.CART_ITEM_NOT_EXIST);
        }

        // 3. 校验库存（若更新数量）
        ProductSku sku = null;
        if (request.getCount() != null) {
            sku = productSkuMapper.selectBySkuId(cartItem.getSkuId());
            if (sku == null || sku.getIsActive() != 1) {
                throw new BusinessException(ErrorCode.SKU_NOT_EXIST);
            }
            // 库存不足校验
            if (sku.getStock() < request.getCount()) {
                throw new BusinessException(ErrorCode.CART_ITEM_STOCK_NOT_ENOUGH);
            }
        }

        // 4. 构建更新实体
        ShoppingCart updateCart = new ShoppingCart();
        updateCart.setId(cartItem.getId()); // 购物车ID
        updateCart.setUserId(userId); // 用户ID（用于权限校验）
        if (request.getCount() != null) {
            updateCart.setQuantity(request.getCount()); // 更新数量
        }
        if (request.getSelected() != null) {
            updateCart.setSelected(request.getSelected()); // 更新选中状态
        }

        // 5. 执行更新
        int updateResult = shoppingCartMapper.updateCartItem(updateCart);
        if (updateResult <= 0) {
            throw new BusinessException("更新购物车失败，请稍后重试");
        }

        // 6. 查询更新后的完整数据并返回
        ShoppingCart updatedCart = shoppingCartMapper.selectByIdAndUserId(request.getId(), userId);
        return convertToCartItemResponse(updatedCart);
    }

    // 新增：批量删除购物车商品
    @Override
    @Transactional
    public CartDeleteResponse deleteCartItems(Long userId, CartDeleteRequest request) {
        // 1. 参数校验
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }
        if (request.getIds() == null || request.getIds().isEmpty()) {
            throw new BusinessException("购物车ID列表不能为空");
        }

        // 2. 校验购物车条目归属权（仅允许删除当前用户的购物车项）
        List<ShoppingCart> cartItems = shoppingCartMapper.selectByIds(request.getIds(), userId);
        if (cartItems.size() != request.getIds().size()) {
            log.warn("用户 {} 尝试删除不属于自己的购物车项，请求ID数：{}，实际归属ID数：{}",
                    userId, request.getIds().size(), cartItems.size());
            throw new BusinessException("存在无效或不属于当前用户的购物车项");
        }

        // 3. 执行批量删除（复用ShoppingCartMapper已有的batchDelete方法）
        int deletedCount = shoppingCartMapper.batchDelete(request.getIds(), userId);
        if (deletedCount <= 0) {
            throw new BusinessException("删除购物车商品失败，请稍后重试");
        }

        log.info("用户 {} 成功删除购物车项，删除数量：{}，删除ID列表：{}",
                userId, deletedCount, request.getIds());

        // 4. 构建并返回响应
        return new CartDeleteResponse(deletedCount);
    }

    /**
     * 新增：获取购物车商品总数量
     * 逻辑：查询用户所有购物车条目，累加quantity字段得到总数量
     */
    @Override
    public CartCountResponse getCartTotalCount(Long userId) {
        // 1. 参数校验（复用现有校验逻辑，保持一致性）
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }

        // 2. 查询用户购物车列表（复用现有查询逻辑，避免重复代码）
        List<ShoppingCart> cartList = shoppingCartMapper.selectByUserId(userId);

        // 3. 统计总数量（累加所有条目的quantity）
        Integer totalCount = cartList.stream()
                .mapToInt(ShoppingCart::getQuantity) // 提取每个条目的数量
                .sum(); // 求和

        log.info("用户 {} 购物车商品总数量：{}", userId, totalCount);

        // 4. 封装响应数据
        return new CartCountResponse(totalCount);
    }

    /**
     * 批量更新购物车选中状态（全选/取消全选）
     */
    @Override
    @Transactional
    public CartBatchSelectResponse batchUpdateSelectedStatus(Long userId, CartBatchSelectRequest request) {
        // 1. 参数校验（复用现有校验逻辑，保持一致性）
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }
        if (request.getIds() == null || request.getIds().isEmpty()) {
            throw new BusinessException("购物车条目ID列表不能为空");
        }
        if (request.getSelected() == null) {
            throw new BusinessException("选中状态不能为空");
        }

        // 2. 校验购物车条目归属权（仅允许更新当前用户的购物车项）
        List<ShoppingCart> cartItems = shoppingCartMapper.selectByIds(request.getIds(), userId);
        if (cartItems.size() != request.getIds().size()) {
            log.warn("用户 {} 尝试更新不属于自己的购物车项，请求ID数：{}，实际归属ID数：{}",
                    userId, request.getIds().size(), cartItems.size());
            throw new BusinessException("存在无效或不属于当前用户的购物车项");
        }

        // 3. 执行批量更新（复用ShoppingCartMapper新增的batchUpdateSelectedStatus方法）
        int updatedCount = shoppingCartMapper.batchUpdateSelectedStatus(
                request.getIds(),
                userId,
                request.getSelected()
        );
        if (updatedCount <= 0) {
            throw new BusinessException("批量更新选中状态失败，请稍后重试");
        }

        log.info("用户 {} 成功批量更新购物车选中状态，更新数量：{}，目标状态：{}，更新ID列表：{}",
                userId, updatedCount, request.getSelected(), request.getIds());

        // 4. 构建并返回响应
        return new CartBatchSelectResponse(updatedCount);
    }
}