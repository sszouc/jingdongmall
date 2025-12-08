// src/main/java/com/jingdong/mall/mapper/ShoppingCartMapper.java
package com.jingdong.mall.mapper;

import com.jingdong.mall.model.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {
    /**
     * 根据用户ID查询购物车列表
     */
    @Select("SELECT id, user_id, sku_id, quantity, selected, created_time, updated_time " +
            "FROM shopping_cart " +
            "WHERE user_id = #{userId} AND quantity > 0 " +
            "ORDER BY updated_time DESC")
    List<ShoppingCart> selectByUserId(@Param("userId") Long userId);
}