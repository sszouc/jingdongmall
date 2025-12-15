package com.jingdong.mall.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单列表响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderListResponse {

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 当前页码
     */
    private Integer page;

    /**
     * 每页数量
     */
    private Integer pageSize;

    /**
     * 订单列表
     */
    private List<OrderPreviewDTO> orders;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderPreviewDTO {
        /**
         * 订单号
         */
        private String orderSn;

        /**
         * 总金额
         */
        private BigDecimal totalAmount;

        /**
         * 实付金额
         */
        private BigDecimal payAmount;

        /**
         * 订单状态
         */
        private String status;

        /**
         * 创建时间
         */
        private LocalDateTime createdAt;

        /**
         * 订单商品种类数
         */
        private Integer itemCount;

        /**
         * 预览商品列表
         */
        private List<PreviewItemDTO> previewItems;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PreviewItemDTO {
        /**
         * 商品名称
         */
        private String productName;

        /**
         * 商品主图
         */
        private String mainImage;

        /**
         * 购买数量
         */
        private Integer quantity;
    }
}