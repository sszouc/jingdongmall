package com.jingdong.mall.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jingdong.mall.common.exception.BusinessException;
import com.jingdong.mall.common.exception.ErrorCode;
import com.jingdong.mall.mapper.*;
import com.jingdong.mall.model.dto.request.OrderCreateRequest;
import com.jingdong.mall.model.dto.request.OrderCreateFromCartRequest;
import com.jingdong.mall.model.dto.response.OrderCreateResponse;
import com.jingdong.mall.model.entity.*;
import com.jingdong.mall.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private ProductSkuMapper productSkuMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    @Transactional
    public OrderCreateResponse createOrderFromCart(Long userId, OrderCreateFromCartRequest request) {
        log.info("用户 {} 开始创建订单，购物车项ID: {}", userId, request.getCartItemIds());

        try {
            // 1. 验证地址
            Address address = validateAddress(userId, request.getAddressId());

            // 2. 查询购物车项
            List<ShoppingCart> cartItems = shoppingCartMapper.selectByIds(request.getCartItemIds(), userId);
            if (cartItems == null || cartItems.isEmpty()) {
                throw new BusinessException(ErrorCode.CART_ITEM_NOT_EXIST);
            }

            // 3. 验证库存并计算总金额
            List<OrderItem> orderItems = new ArrayList<>();
            BigDecimal totalAmount = BigDecimal.ZERO;

            for (ShoppingCart cartItem : cartItems) {
                // 验证SKU
                ProductSku sku = productSkuMapper.selectBySkuId(cartItem.getSkuId());
                if (sku == null || sku.getIsActive() != 1) {
                    throw new BusinessException(ErrorCode.SKU_NOT_EXIST);
                }

                // 验证库存
                if (sku.getStock() < cartItem.getQuantity()) {
                    throw new BusinessException(ErrorCode.CART_ITEM_STOCK_NOT_ENOUGH);
                }

                // 查询商品信息
                Product product = productMapper.selectById(sku.getProductId());
                if (product == null || product.getIsActive() != 1) {
                    throw new BusinessException(ErrorCode.PRODUCT_NOT_EXIST);
                }

                // 创建订单项
                OrderItem orderItem = new OrderItem();
                orderItem.setSkuId(sku.getId());
                orderItem.setProductName(product.getName());
                orderItem.setPrice(sku.getPrice());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setTotalPrice(sku.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));

                // 设置SKU规格
                Map<String, String> specs = new HashMap<>();
                if (sku.getOs() != null) specs.put("操作系统", sku.getOs());
                if (sku.getCpu() != null) specs.put("处理器", sku.getCpu());
                if (sku.getRam() != null) specs.put("内存容量", sku.getRam());
                if (sku.getStorage() != null) specs.put("存储容量", sku.getStorage());
                if (sku.getGpu() != null) specs.put("显卡", sku.getGpu());
                orderItem.setSkuSpecs(objectMapper.writeValueAsString(specs));

                // 设置商品主图
                List<String> images = objectMapper.readValue(product.getMainImages(), new TypeReference<List<String>>() {});
                orderItem.setMainImage(images != null && !images.isEmpty() ? images.get(0) : "");

                orderItems.add(orderItem);
                totalAmount = totalAmount.add(orderItem.getTotalPrice());
            }

            // 4. 计算运费和优惠（这里简化处理，实际业务需要复杂计算）
            BigDecimal shippingFee = calculateShippingFee(totalAmount, address);
            BigDecimal discountAmount = calculateDiscountAmount(userId, totalAmount);
            BigDecimal payAmount = totalAmount.add(shippingFee).subtract(discountAmount);

            // 5. 生成订单号
            String orderSn = generateOrderSn();

            // 6. 创建订单
            Order order = new Order();
            order.setOrderSn(orderSn);
            order.setUserId(userId);
            order.setTotalAmount(totalAmount);
            order.setShippingFee(shippingFee);
            order.setDiscountAmount(discountAmount);
            order.setPayAmount(payAmount);

            // 设置收货地址
            order.setReceiverName(address.getName());
            order.setReceiverPhone(address.getPhone());
            order.setReceiverProvince(address.getProvince());
            order.setReceiverCity(address.getCity());
            order.setReceiverDistrict(address.getDistrict());
            order.setReceiverDetail(address.getDetail());
            order.setReceiverPostalCode(address.getPostalCode());

            order.setStatus(0); // 0: 待付款
            order.setPaymentMethod(0); // 0: 未支付
            order.setBuyerRemark(request.getBuyerRemark());

            int orderResult = orderMapper.insert(order);
            if (orderResult <= 0) {
                throw new BusinessException(ErrorCode.ORDER_CREATE_FAILED);
            }

            // 7. 批量插入订单项
            for (OrderItem item : orderItems) {
                item.setOrderId(order.getId());
            }

            int itemResult = orderItemMapper.batchInsert(orderItems);
            if (itemResult != orderItems.size()) {
                throw new BusinessException(ErrorCode.ORDER_CREATE_FAILED);
            }

            // 8. 删除购物车项
            List<Integer> cartItemIds = cartItems.stream()
                    .map(item -> item.getId().intValue())
                    .collect(Collectors.toList());
            shoppingCartMapper.batchDelete(cartItemIds, userId);

            // 9. 扣减库存（实际业务可能需要预扣库存，支付成功后再真正扣减）
            // 这里简化处理，直接扣减
            // 注意：实际业务中需要考虑库存锁定机制

            // 10. 返回响应
            OrderCreateResponse response = new OrderCreateResponse();
            response.setOrderSn(orderSn);
            response.setTotalAmount(totalAmount);
            response.setPayAmount(payAmount);
            response.setExpiresIn(1800); // 30分钟支付时间

            log.info("订单创建成功: orderSn={}, userId={}, totalAmount={}", orderSn, userId, totalAmount);

            return response;

        } catch (BusinessException e) {
            log.warn("创建订单业务异常: userId={}, message={}", userId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("创建订单系统异常", e);
            throw new BusinessException(ErrorCode.ORDER_CREATE_FAILED);
        }
    }

    @Override
    @Transactional
    public OrderCreateResponse createOrder(Long userId, OrderCreateRequest request) {
        log.info("用户 {} 开始创建单个商品订单，specId: {}, quantity: {}",
                userId, request.getSpecId(), request.getQuantity());

        try {
            // 1. 验证地址
            Address address = validateAddress(userId, request.getAddressId());

            // 2. 验证商品规格
            ProductSku sku = productSkuMapper.selectBySkuId(request.getSpecId());
            if (sku == null || sku.getIsActive() != 1) {
                throw new BusinessException(ErrorCode.SKU_NOT_EXIST);
            }

            // 3. 验证库存
            if (sku.getStock() < request.getQuantity()) {
                throw new BusinessException(ErrorCode.PRODUCT_STOCK_NOT_ENOUGH);
            }

            // 4. 查询商品信息
            Product product = productMapper.selectById(sku.getProductId());
            if (product == null || product.getIsActive() != 1) {
                throw new BusinessException(ErrorCode.PRODUCT_NOT_EXIST);
            }

            // 5. 创建订单项
            List<OrderItem> orderItems = new ArrayList<>();
            BigDecimal totalAmount = BigDecimal.ZERO;

            // 创建订单项
            OrderItem orderItem = new OrderItem();
            orderItem.setSkuId(sku.getId());
            orderItem.setProductName(product.getName());
            orderItem.setPrice(sku.getPrice());
            orderItem.setQuantity(request.getQuantity());
            orderItem.setTotalPrice(sku.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())));

            // 设置SKU规格
            Map<String, String> specs = new HashMap<>();
            if (sku.getOs() != null) specs.put("操作系统", sku.getOs());
            if (sku.getCpu() != null) specs.put("处理器", sku.getCpu());
            if (sku.getRam() != null) specs.put("内存容量", sku.getRam());
            if (sku.getStorage() != null) specs.put("存储容量", sku.getStorage());
            if (sku.getGpu() != null) specs.put("显卡", sku.getGpu());
            orderItem.setSkuSpecs(objectMapper.writeValueAsString(specs));

            // 设置商品主图
            List<String> images = objectMapper.readValue(product.getMainImages(), new TypeReference<List<String>>() {});
            orderItem.setMainImage(images != null && !images.isEmpty() ? images.get(0) : "");

            orderItems.add(orderItem);
            totalAmount = totalAmount.add(orderItem.getTotalPrice());

            // 6. 计算运费和优惠
            BigDecimal shippingFee = calculateShippingFee(totalAmount, address);
            BigDecimal discountAmount = calculateDiscountAmount(userId, totalAmount);
            BigDecimal payAmount = totalAmount.add(shippingFee).subtract(discountAmount);

            // 7. 生成订单号
            String orderSn = generateOrderSn();

            // 8. 创建订单
            Order order = new Order();
            order.setOrderSn(orderSn);
            order.setUserId(userId);
            order.setTotalAmount(totalAmount);
            order.setShippingFee(shippingFee);
            order.setDiscountAmount(discountAmount);
            order.setPayAmount(payAmount);

            // 设置收货地址
            order.setReceiverName(address.getName());
            order.setReceiverPhone(address.getPhone());
            order.setReceiverProvince(address.getProvince());
            order.setReceiverCity(address.getCity());
            order.setReceiverDistrict(address.getDistrict());
            order.setReceiverDetail(address.getDetail());
            order.setReceiverPostalCode(address.getPostalCode());

            order.setStatus(0); // 0: 待付款
            order.setPaymentMethod(0); // 0: 未支付
            order.setBuyerRemark(request.getBuyerRemark());

            int orderResult = orderMapper.insert(order);
            if (orderResult <= 0) {
                throw new BusinessException(ErrorCode.ORDER_CREATE_FAILED);
            }

            // 9. 批量插入订单项
            for (OrderItem item : orderItems) {
                item.setOrderId(order.getId());
            }

            int itemResult = orderItemMapper.batchInsert(orderItems);
            if (itemResult != orderItems.size()) {
                throw new BusinessException(ErrorCode.ORDER_CREATE_FAILED);
            }

            // 10. 扣减库存（实际业务中可能需要预扣库存，这里简化处理）
            // TODO: 实际业务中需要考虑库存锁定机制

            // 11. 返回响应
            OrderCreateResponse response = new OrderCreateResponse();
            response.setOrderSn(orderSn);
            response.setTotalAmount(totalAmount);
            response.setPayAmount(payAmount);
            response.setExpiresIn(1800); // 30分钟支付时间

            log.info("单个商品订单创建成功: orderSn={}, userId={}, totalAmount={}",
                    orderSn, userId, totalAmount);

            return response;

        } catch (BusinessException e) {
            log.warn("创建单个商品订单业务异常: userId={}, message={}", userId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("创建单个商品订单系统异常", e);
            throw new BusinessException(ErrorCode.ORDER_CREATE_FAILED);
        }
    }

    private Address validateAddress(Long userId, Integer addressId) {
        log.info("验证地址：userId={}, addressId={}", userId, addressId);
        if (addressId == null) {
            throw new BusinessException(ErrorCode.ADDRESS_NOT_EXIST);
        }

        List<Address> addresses = addressMapper.selectByUserId(userId);
        log.info("用户地址列表：{}", addresses);

        Address address = addresses.stream()
                .filter(a -> a.getId().equals(addressId))
                .findFirst()
                .orElse(null);

        if (address == null) {
            log.warn("地址验证失败：地址不存在或不属于当前用户");
            throw new BusinessException(ErrorCode.ADDRESS_NOT_EXIST);
        }

        return address;
    }

    private BigDecimal calculateShippingFee(BigDecimal totalAmount, Address address) {
        // 简化处理：满99元免运费
        if (totalAmount.compareTo(new BigDecimal("99")) >= 0) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal("10"); // 默认运费10元
    }

    private BigDecimal calculateDiscountAmount(Long userId, BigDecimal totalAmount) {
        // 简化处理：无优惠
        return BigDecimal.ZERO;
    }

    private String generateOrderSn() {
        // 生成订单号：时间戳 + 随机数
        // 实际项目中应使用分布式ID生成器
        String timestamp = String.valueOf(System.currentTimeMillis());
        String random = String.valueOf((int)(Math.random() * 9000) + 1000);
        return timestamp + random;
    }
}