package com.jingdong.mall.mapper;

import com.jingdong.mall.model.entity.Order;
import org.apache.ibatis.annotations.*;

@Mapper
public interface OrderMapper {

    /**
     * 插入订单
     */
    @Insert("INSERT INTO `order` (order_sn, user_id, total_amount, discount_amount, shipping_fee, pay_amount, " +
            "receiver_name, receiver_phone, receiver_province, receiver_city, receiver_district, receiver_detail, " +
            "receiver_postal_code, status, payment_method, buyer_remark, created_time, updated_time) " +
            "VALUES (#{orderSn}, #{userId}, #{totalAmount}, #{discountAmount}, #{shippingFee}, #{payAmount}, " +
            "#{receiverName}, #{receiverPhone}, #{receiverProvince}, #{receiverCity}, #{receiverDistrict}, " +
            "#{receiverDetail}, #{receiverPostalCode}, #{status}, #{paymentMethod}, #{buyerRemark}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Order order);

    /**
     * 根据订单号查询订单
     */
    @Select("SELECT * FROM `order` WHERE order_sn = #{orderSn}")
    Order selectByOrderSn(String orderSn);

    /**
     * 根据订单号和用户ID查询订单
     */
    @Select("SELECT * FROM `order` WHERE order_sn = #{orderSn} AND user_id = #{userId}")
    Order selectByOrderSnAndUserId(@Param("orderSn") String orderSn, @Param("userId") Long userId);
}