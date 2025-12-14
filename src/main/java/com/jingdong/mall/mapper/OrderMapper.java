package com.jingdong.mall.mapper;

import com.jingdong.mall.model.dto.request.OrderListRequest;
import com.jingdong.mall.model.entity.Order;
import com.jingdong.mall.provider.OrderSqlProvider;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

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

    /**
     * 分页查询订单列表
     * @param request 查询条件
     * @param userId 用户ID
     * @return 订单列表
     */
    @SelectProvider(type = OrderSqlProvider.class, method = "selectOrderList")
    List<Order> selectOrderList(@Param("request") OrderListRequest request, @Param("userId") Long userId);

    /**
     * 统计符合条件的订单总数
     * @param request 查询条件
     * @param userId 用户ID
     * @return 订单总数
     */
    @SelectProvider(type = OrderSqlProvider.class, method = "countOrderList")
    Long countOrderList(@Param("request") OrderListRequest request, @Param("userId") Long userId);

    /**
     * 批量查询订单预览项
     * @param orderIds 订单ID列表
     * @return 预览项列表
     */
    @SelectProvider(type = OrderSqlProvider.class, method = "selectPreviewItemsByOrderIds")
    List<Map<String, Object>> selectPreviewItemsByOrderIds(@Param("orderIds") List<Long> orderIds);

    /**
     * 统计每个订单的商品种类数
     * @param orderIds 订单ID列表
     * @return 订单ID和商品种类数的映射
     */
    @SelectProvider(type = OrderSqlProvider.class, method = "countItemsByOrderIds")
    List<Map<String, Object>> countItemsByOrderIds(@Param("orderIds") List<Long> orderIds);

    /**
     * 根据ID删除订单
     * @param id 订单ID
     * @return 删除的行数
     */
    @Delete("DELETE FROM `order` WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
}