package com.jingdong.mall.mapper;

import com.jingdong.mall.model.entity.OrderItem;
import com.jingdong.mall.provider.OrderItemSqlProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderItemMapper {

    /**
     * 批量插入订单项
     */
    @InsertProvider(type = OrderItemSqlProvider.class, method = "batchInsert")
    int batchInsert(@Param("orderItems") List<OrderItem> orderItems);
}