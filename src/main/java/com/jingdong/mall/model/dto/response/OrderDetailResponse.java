package com.jingdong.mall.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单详情响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailResponse {

    /**
     * 订单详情
     */
    private OrderDetailDTO order;

    /**
     * 订单商品项列表
     */
    private List<OrderItemDTO> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderDetailDTO {
        private Long id; // 订单的数据库id
        private String orderSn; // 订单号
        private Long userId; // 订单所属的用户id
        private BigDecimal totalAmount; // 总价
        private BigDecimal discountAmount; // 折扣金额
        private BigDecimal shippingFee; // 运费
        private BigDecimal payAmount; // 实付金额
        private String receiverName; // 收货人姓名
        private String receiverPhone; // 收货人电话
        private String receiverProvince; // 省份
        private String receiverCity; // 城市
        private String receiverDistrict; // 区县
        private String receiverDetail; // 详细地址
        private String receiverPostalCode; // 邮政编码
        private String status; // 订单状态
        private String paymentMethod; // 支付方式
        private LocalDateTime payTime; // 支付时间
        private String shippingMethod; // 配送方式
        private String trackingNumber; // 快递单号
        private LocalDateTime shippingTime; // 发货时间
        private LocalDateTime confirmTime; // 确认收货时间
        private LocalDateTime cancelTime; // 取消时间
        private String cancelReason; // 取消原因
        private LocalDateTime refundTime; // 申请退款时间
        private String refundReason; // 退款原因
        private String buyerRemark; // 买家留言
        private LocalDateTime createdAt; // 创建时间
        private LocalDateTime updatedAt; // 更新时间
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemDTO {
        private Long id; // 商品id（order_item表的id）
        private Integer skuId; // skuId
        private String productName; // 商品名称
        private Object skuSpecs; // 商品规格（JSON格式）
        private String mainImage; // 主图URL
        private BigDecimal price; // 单价
        private Integer quantity; // 数量
        private BigDecimal totalPrice; // 总价
    }
}