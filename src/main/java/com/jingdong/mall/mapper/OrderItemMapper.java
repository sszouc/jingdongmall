package com.jingdong.mall.mapper;

import com.jingdong.mall.model.entity.OrderItem;
import com.jingdong.mall.provider.OrderItemSqlProvider;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderItemMapper {

    /**
     * 批量插入订单项
     */
    @InsertProvider(type = OrderItemSqlProvider.class, method = "batchInsert")
    int batchInsert(@Param("orderItems") List<OrderItem> orderItems);

    /**
     * 根据订单ID查询订单项列表
     */
    @Select("SELECT * FROM order_item WHERE order_id = #{orderId} ORDER BY id ASC")
    List<OrderItem> selectByOrderId(@Param("orderId") Long orderId);
}