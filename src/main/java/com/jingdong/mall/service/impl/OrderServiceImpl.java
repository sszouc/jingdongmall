package com.jingdong.mall.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jingdong.mall.common.exception.BusinessException;
import com.jingdong.mall.common.exception.ErrorCode;
import com.jingdong.mall.mapper.*;
import com.jingdong.mall.model.dto.request.OrderCreateRequest;
import com.jingdong.mall.model.dto.request.OrderCreateFromCartRequest;
import com.jingdong.mall.model.dto.request.OrderUpdateRequest;
import com.jingdong.mall.model.dto.response.OrderUpdateResponse;
import com.jingdong.mall.model.dto.response.OrderCreateResponse;
import com.jingdong.mall.model.dto.response.OrderDeleteResponse;
import com.jingdong.mall.model.dto.response.OrderDetailResponse;
import com.jingdong.mall.model.dto.request.OrderListRequest;
import com.jingdong.mall.model.dto.response.OrderListResponse;
import com.jingdong.mall.model.entity.*;
import com.jingdong.mall.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    public OrderListResponse getOrderList(Long userId, OrderListRequest request) {
        try {
            log.info("获取订单列表: userId={}, page={}, pageSize={}, status={}",
                    userId, request.getPage(), request.getPageSize(), request.getStatus());

            // 1. 分页查询订单列表
            List<Order> orders = orderMapper.selectOrderList(request, userId);

            if (orders == null || orders.isEmpty()) {
                return buildEmptyResponse(request);
            }

            // 2. 收集订单ID
            List<Long> orderIds = orders.stream()
                    .map(Order::getId)
                    .collect(Collectors.toList());

            // 3. 批量查询每个订单的商品种类数
            List<Map<String, Object>> itemCounts = orderMapper.countItemsByOrderIds(orderIds);
            Map<Long, Integer> orderItemCountMap = new HashMap<>();
            for (Map<String, Object> item : itemCounts) {
                Long orderId = ((Number) item.get("order_id")).longValue();
                Integer count = ((Number) item.get("item_count")).intValue();
                orderItemCountMap.put(orderId, count);
            }

            // 4. 批量查询订单预览项（每个订单最多3个）
            List<Map<String, Object>> previewItems = orderMapper.selectPreviewItemsByOrderIds(orderIds);

            // 按订单ID分组预览项
            Map<Long, List<OrderListResponse.PreviewItemDTO>> previewItemsMap = new HashMap<>();
            for (Map<String, Object> item : previewItems) {
                Long orderId = ((Number) item.get("order_id")).longValue();

                // 每个订单最多取3个预览项
                List<OrderListResponse.PreviewItemDTO> items = previewItemsMap.computeIfAbsent(
                        orderId, k -> new ArrayList<>());

                if (items.size() < 3) {
                    OrderListResponse.PreviewItemDTO previewItem = new OrderListResponse.PreviewItemDTO();
                    previewItem.setProductName((String) item.get("product_name"));
                    previewItem.setMainImage((String) item.get("main_image"));
                    previewItem.setQuantity(((Number) item.get("quantity")).intValue());
                    items.add(previewItem);
                }
            }

            // 5. 构建订单预览列表
            List<OrderListResponse.OrderPreviewDTO> orderPreviews = orders.stream()
                    .map(order -> convertToOrderPreviewDTO(order, orderItemCountMap, previewItemsMap))
                    .collect(Collectors.toList());

            // 6. 查询总记录数
            Long total = orderMapper.countOrderList(request, userId);

            // 7. 构建响应
            OrderListResponse response = new OrderListResponse();
            response.setTotal(total);
            response.setPage(request.getPage());
            response.setPageSize(request.getPageSize());
            response.setOrders(orderPreviews);

            log.info("获取订单列表成功: userId={}, total={}, currentSize={}",
                    userId, total, orderPreviews.size());
            return response;

        } catch (BusinessException e) {
            log.warn("获取订单列表业务异常: userId={}, message={}", userId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("获取订单列表系统异常: userId={}", userId, e);
            throw new BusinessException(ErrorCode.ORDER_LIST_GET_FAILED);
        }
    }

    /**
     * 构建空响应
     */
    private OrderListResponse buildEmptyResponse(OrderListRequest request) {
        OrderListResponse response = new OrderListResponse();
        response.setTotal(0L);
        response.setPage(request.getPage());
        response.setPageSize(request.getPageSize());
        response.setOrders(new ArrayList<>());
        return response;
    }

    /**
     * 将Order实体转换为OrderPreviewDTO
     */
    private OrderListResponse.OrderPreviewDTO convertToOrderPreviewDTO(
            Order order,
            Map<Long, Integer> itemCountMap,
            Map<Long, List<OrderListResponse.PreviewItemDTO>> previewItemsMap) {

        OrderListResponse.OrderPreviewDTO dto = new OrderListResponse.OrderPreviewDTO();
        dto.setOrderSn(order.getOrderSn());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setPayAmount(order.getPayAmount());
        dto.setStatus(getStatusText(order.getStatus()));
        dto.setCreatedAt(order.getCreatedTime());
        dto.setItemCount(itemCountMap.getOrDefault(order.getId(), 0));
        dto.setPreviewItems(previewItemsMap.getOrDefault(order.getId(), new ArrayList<>()));

        return dto;
    }

    /**
     * 获取订单状态文本
     */
    private String getStatusText(Integer status) {
        if (status == null) return "未知";

        return switch (status) {
            case 0 -> "待付款";
            case 1 -> "待发货";
            case 2 -> "待收货";
            case 3 -> "已完成";
            case 4 -> "已取消";
            case 5 -> "退款中";
            case 6 -> "退款成功";
            case 7 -> "退款失败";
            default -> "未知";
        };
    }

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

    @Override
    @Transactional
    public OrderUpdateResponse updateOrderStatus(Long userId, String orderSn, OrderUpdateRequest request) {
        log.info("更新订单状态: userId={}, orderSn={}, action={}, reason={}",
                userId, orderSn, request.getAction(), request.getReason());

        // 1. 查询订单是否存在且属于该用户
        Order order = orderMapper.selectByOrderSnAndUserId(orderSn, userId);
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_EXIST);
        }

        // 2. 根据操作类型执行不同的状态更新逻辑
        Integer currentStatus = order.getStatus();
        Integer action = request.getAction();

        switch (action) {
            case 0: // 确认收货
                confirmReceipt(order, currentStatus);
                break;
            case 1: // 取消订单
                cancelOrder(order, currentStatus, request.getReason());
                break;
            case 2: // 申请退款
                applyRefund(order, currentStatus, request.getReason());
                break;
            case 3: // 支付订单
                payOrder(order, currentStatus);
                break;
            default:
                throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR, "不支持的操作类型");
        }

        // 3. 更新订单
        int result = updateOrderStatusInDb(order);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR, "订单状态更新失败");
        }

        // 4. 构建响应
        OrderUpdateResponse response = new OrderUpdateResponse();
        response.setOrderSn(orderSn);
        response.setStatus(order.getStatus());
        response.setStatusText(getStatusText(order.getStatus()));
        response.setUpdatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        log.info("订单状态更新成功: orderSn={}, 新状态={}", orderSn, order.getStatus());
        return response;
    }

    /**
     * 确认收货
     */
    private void confirmReceipt(Order order, Integer currentStatus) {
        // 确认收货只能从待收货(2)状态进行
        if (currentStatus != 2) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR,
                    "只有待收货状态的订单才能确认收货");
        }

        order.setStatus(3); // 3: 已完成
        order.setConfirmTime(LocalDateTime.now());
    }

    /**
     * 取消订单
     */
    private void cancelOrder(Order order, Integer currentStatus, String reason) {
        // 取消订单只能从待付款(0)状态进行
        if (currentStatus != 0) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR,
                    "只有待付款状态的订单才能取消");
        }

        order.setStatus(4); // 4: 已取消
        order.setCancelTime(LocalDateTime.now());
        order.setCancelReason(reason);
    }

    /**
     * 申请退款
     */
    private void applyRefund(Order order, Integer currentStatus, String reason) {
        // 申请退款只能从待发货(1)状态进行
        if (currentStatus != 1) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR,
                    "只有待发货状态的订单才能申请退款");
        }

        order.setStatus(5); // 5: 退款中
        order.setRefundTime(LocalDateTime.now());
        order.setRefundReason(reason);
    }

    /**
     * 支付订单
     */
    private void payOrder(Order order, Integer currentStatus) {
        // 支付订单只能从待付款(0)状态进行
        if (currentStatus != 0) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR,
                    "只有待付款状态的订单才能支付");
        }

        order.setStatus(1); // 1: 待发货
        order.setPayTime(LocalDateTime.now());
    }

    /**
     * 更新订单状态到数据库
     */
    private int updateOrderStatusInDb(Order order) {
        // 使用动态SQL更新订单状态及相关的字段
        return orderMapper.updateOrderStatus(order);
    }

    @Override
    @Transactional
    public OrderDeleteResponse deleteHistoricalOrder(Long userId, String orderSn) {
        log.info("用户 {} 请求删除订单: {}", userId, orderSn);

        // 1. 查询订单是否存在且属于该用户
        Order order = orderMapper.selectByOrderSnAndUserId(orderSn, userId);
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_EXIST);
        }

        // 2. 检查订单状态是否允许删除
        // 允许删除的状态：3-已完成、4-已取消、6-退款成功、7-退款失败
        Integer status = order.getStatus();
        if (!isDeletableStatus(status)) {
            throw new BusinessException(ErrorCode.ORDER_CANNOT_DELETE,
                    "只能删除已完成、已取消、退款成功或退款失败的订单");
        }

        // 3. 删除订单（级联删除order_item表中的记录）
        // 由于外键约束是ON DELETE CASCADE，删除订单会自动删除订单项
        int result = orderMapper.deleteById(order.getId());
        if (result <= 0) {
            throw new BusinessException(ErrorCode.ORDER_DELETE_FAILED);
        }

        log.info("用户 {} 成功删除订单: {}", userId, orderSn);

        // 4. 返回删除响应
        OrderDeleteResponse response = new OrderDeleteResponse();
        response.setOrderSn(orderSn);
        response.setDeletedTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        return response;
    }

    /**
     * 检查订单状态是否允许删除
     * @param status 订单状态
     * @return true表示允许删除
     */
    private boolean isDeletableStatus(Integer status) {
        return status != null && (status == 3 || status == 4 || status == 6 || status == 7);
    }



    @Override
    public OrderDetailResponse getOrderDetail(Long userId, String orderSn) {
        try {
            log.info("获取订单详情: userId={}, orderSn={}", userId, orderSn);

            // 1. 验证订单号和用户ID
            if (orderSn == null || orderSn.trim().isEmpty()) {
                throw new BusinessException("订单号不能为空");
            }

            // 2. 查询订单基本信息（确保订单属于当前用户）
            Order order = orderMapper.selectByOrderSnAndUserId(orderSn, 3L);
            if (order == null) {
                throw new BusinessException(ErrorCode.ORDER_NOT_EXIST);
            }

            // 3. 查询订单项列表
            List<OrderItem> orderItems = orderItemMapper.selectByOrderId(order.getId());

            // 4. 构建响应
            OrderDetailResponse response = new OrderDetailResponse();
            response.setOrder(convertToOrderDetailDTO(order));
            response.setItems(convertToOrderItemDTOs(orderItems));

            return response;

        } catch (BusinessException e) {
            log.warn("获取订单详情业务异常: userId={}, orderSn={}, message={}",
                    userId, orderSn, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("获取订单详情系统异常: userId={}, orderSn={}", userId, orderSn, e);
            throw new BusinessException("获取订单详情失败，请稍后重试");
        }
    }

    /**
     * 将Order实体转换为OrderDetailDTO
     */
    private OrderDetailResponse.OrderDetailDTO convertToOrderDetailDTO(Order order) {
        OrderDetailResponse.OrderDetailDTO dto = new OrderDetailResponse.OrderDetailDTO();

        dto.setId(order.getId());
        dto.setOrderSn(order.getOrderSn());
        dto.setUserId(order.getUserId());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setDiscountAmount(order.getDiscountAmount());
        dto.setShippingFee(order.getShippingFee());
        dto.setPayAmount(order.getPayAmount());
        dto.setReceiverName(order.getReceiverName());
        dto.setReceiverPhone(order.getReceiverPhone());
        dto.setReceiverProvince(order.getReceiverProvince());
        dto.setReceiverCity(order.getReceiverCity());
        dto.setReceiverDistrict(order.getReceiverDistrict());
        dto.setReceiverDetail(order.getReceiverDetail());
        dto.setReceiverPostalCode(order.getReceiverPostalCode());

        // 转换状态和支付方式为字符串
        dto.setStatus(convertOrderStatusToString(order.getStatus()));
        dto.setPaymentMethod(convertPaymentMethodToString(order.getPaymentMethod()));

        dto.setPayTime(order.getPayTime());
        dto.setShippingMethod(order.getShippingMethod());
        dto.setTrackingNumber(order.getTrackingNumber());
        dto.setShippingTime(order.getShippingTime());
        dto.setConfirmTime(order.getConfirmTime());
        dto.setCancelTime(order.getCancelTime());
        dto.setCancelReason(order.getCancelReason());
        dto.setBuyerRemark(order.getBuyerRemark());
        dto.setCreatedAt(order.getCreatedTime());
        dto.setUpdatedAt(order.getUpdatedTime());

        return dto;
    }

    /**
     * 将订单状态码转换为字符串
     */
    private String convertOrderStatusToString(Integer status) {
        if (status == null) {
            return "未知";
        }

        return switch (status) {
            case 0 -> "待付款";
            case 1 -> "待发货";
            case 2 -> "待收货";
            case 3 -> "已完成";
            case 4 -> "已取消";
            case 5 -> "退款中";
            case 6 -> "退款成功";
            case 7 -> "退款失败";
            default -> "未知";
        };
    }

    /**
     * 将支付方式码转换为字符串
     */
    private String convertPaymentMethodToString(Integer paymentMethod) {
        if (paymentMethod == null) {
            return "未支付";
        }

        return switch (paymentMethod) {
            case 0 -> "未支付";
            case 1 -> "支付宝";
            case 2 -> "微信支付";
            case 3 -> "银行卡";
            default -> "其他";
        };
    }

    /**
     * 将OrderItem列表转换为OrderItemDTO列表
     */
    private List<OrderDetailResponse.OrderItemDTO> convertToOrderItemDTOs(List<OrderItem> orderItems) {
        if (orderItems == null || orderItems.isEmpty()) {
            return new ArrayList<>();
        }

        return orderItems.stream()
                .map(this::convertToOrderItemDTO)
                .collect(Collectors.toList());
    }

    /**
     * 将OrderItem实体转换为OrderItemDTO
     */
    private OrderDetailResponse.OrderItemDTO convertToOrderItemDTO(OrderItem item) {
        OrderDetailResponse.OrderItemDTO dto = new OrderDetailResponse.OrderItemDTO();

        dto.setId(item.getId());
        dto.setSkuId(item.getSkuId());
        dto.setProductName(item.getProductName());
        dto.setMainImage(item.getMainImage());
        dto.setPrice(item.getPrice());
        dto.setQuantity(item.getQuantity());
        dto.setTotalPrice(item.getTotalPrice());

        // 解析规格JSON
        if (item.getSkuSpecs() != null && !item.getSkuSpecs().isEmpty()) {
            try {
                Object specs = objectMapper.readValue(item.getSkuSpecs(), Object.class);
                dto.setSkuSpecs(specs);
            } catch (Exception e) {
                log.warn("解析商品规格JSON失败: {}", item.getSkuSpecs(), e);
                dto.setSkuSpecs(new HashMap<>());
            }
        } else {
            dto.setSkuSpecs(new HashMap<>());
        }

        return dto;
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