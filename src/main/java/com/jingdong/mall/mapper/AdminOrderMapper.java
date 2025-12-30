package com.jingdong.mall.mapper;

import com.jingdong.mall.provider.AdminOrderSqlProvider;
import com.jingdong.mall.model.dto.request.AdminOrderQueryRequest;
import com.jingdong.mall.model.entity.Order;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AdminOrderMapper {

    // 分页查询订单列表 - 使用 Provider
    @SelectProvider(type = AdminOrderSqlProvider.class, method = "selectOrderList")
    List<Order> selectOrderList(@Param("query") AdminOrderQueryRequest query);

    // 统计订单总数 - 使用 Provider
    @SelectProvider(type = AdminOrderSqlProvider.class, method = "countOrderList")
    Long countOrderList(@Param("query") AdminOrderQueryRequest query);

    // 根据订单号查询订单详情（包含用户信息）
    @Select("SELECT o.*, u.username " +
            "FROM `order` o " +
            "LEFT JOIN user u ON o.user_id = u.id " +
            "WHERE o.order_sn = #{orderSn}")
    Order selectByOrderSn(@Param("orderSn") String orderSn);

    // 根据订单号查询订单基本信息（不包含用户信息）
    @Select("SELECT * FROM `order` WHERE order_sn = #{orderSn}")
    Order selectSimpleByOrderSn(@Param("orderSn") String orderSn);

    // 更新订单状态（发货）
    @Update("UPDATE `order` SET " +
            "status = #{status}, " +
            "shipping_method = #{shippingMethod}, " +
            "tracking_number = #{trackingNumber}, " +
            "shipping_time = #{shippingTime}, " +
            "updated_time = NOW() " +
            "WHERE order_sn = #{orderSn}")
    int updateOrderShipping(@Param("orderSn") String orderSn,
                            @Param("status") Integer status,
                            @Param("shippingMethod") String shippingMethod,
                            @Param("trackingNumber") String trackingNumber,
                            @Param("shippingTime") java.time.LocalDateTime shippingTime);

    // 更新订单退款信息
    @Update("UPDATE `order` SET " +
            "status = #{status}, " +
            "refund_time = #{refundTime}, " +
            "refund_reason = #{refundReason}, " +
            "updated_time = NOW() " +
            "WHERE order_sn = #{orderSn}")
    int updateOrderRefund(@Param("orderSn") String orderSn,
                          @Param("status") Integer status,
                          @Param("refundTime") java.time.LocalDateTime refundTime,
                          @Param("refundReason") String refundReason);


    // 查询订单商品数量
    @Select("SELECT COUNT(*) FROM order_item WHERE order_id = #{orderId}")
    Integer countOrderItems(@Param("orderId") String orderId);
}