// src/main/java/com/jingdong/mall/mapper/ShoppingCartMapper.java
package com.jingdong.mall.mapper;

import com.jingdong.mall.model.entity.ShoppingCart;
import org.apache.ibatis.annotations.*;

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

    /**
     * 新增：插入购物车记录
     */
    @Insert("INSERT INTO shopping_cart (user_id, sku_id, quantity, selected, created_time, updated_time) " +
            "VALUES (#{userId}, #{skuId}, #{quantity}, #{selected}, #{createdTime}, #{updatedTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ShoppingCart shoppingCart);

    /**
     * 新增：更新购物车数量和更新时间
     */
    @Update("UPDATE shopping_cart SET " +
            "quantity = #{quantity}, " +
            "updated_time = #{updatedTime} " +
            "WHERE id = #{id} AND user_id = #{userId}")
    int update(ShoppingCart shoppingCart);
}