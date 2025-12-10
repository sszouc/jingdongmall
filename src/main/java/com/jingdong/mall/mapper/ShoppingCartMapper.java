// src/main/java/com/jingdong/mall/mapper/ShoppingCartMapper.java
package com.jingdong.mall.mapper;

import com.jingdong.mall.model.entity.ShoppingCart;
import com.jingdong.mall.provider.ShoppingCartSqlProvider;
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

    /**
     * 根据购物车ID列表查询购物车项
     */
    @SelectProvider(type = ShoppingCartSqlProvider.class, method = "selectByIds")
    List<ShoppingCart> selectByIds(@Param("cartItemIds") List<Integer> cartItemIds, @Param("userId") Long userId);

    /**
     * 批量删除购物车项
     */
    @DeleteProvider(type = ShoppingCartSqlProvider.class, method = "batchDelete")
    int batchDelete(@Param("cartItemIds") List<Integer> cartItemIds, @Param("userId") Long userId);

    /**
     * 更新购物车条目（数量/选中状态）
     * 支持动态更新：只更新非空字段
     */
    @UpdateProvider(type = ShoppingCartSqlProvider.class, method = "updateCartItem")
    int updateCartItem(ShoppingCart shoppingCart);

    /**
     * 根据购物车ID和用户ID查询单条购物车条目
     * 用于更新前校验归属权和有效性
     */
    @Select("SELECT id, user_id, sku_id, quantity, selected, created_time, updated_time " +
            "FROM shopping_cart " +
            "WHERE id = #{cartId} AND user_id = #{userId} AND quantity > 0")
    ShoppingCart selectByIdAndUserId(@Param("cartId") Integer cartId, @Param("userId") Long userId);

    /**
     * 批量更新购物车选中状态
     * @param cartItemIds 购物车条目ID列表
     * @param userId 用户ID
     * @param selected 目标选中状态
     * @return 成功更新的数量
     */
    @UpdateProvider(type = ShoppingCartSqlProvider.class, method = "batchUpdateSelectedStatus")
    int batchUpdateSelectedStatus(@Param("cartItemIds") List<Integer> cartItemIds,
                                  @Param("userId") Long userId,
                                  @Param("selected") Boolean selected);

    /**
     * 按用户ID和SKU ID删除购物车条目
     * @param userId 用户ID（从Token解析）
     * @param skuId SKU ID（从请求参数获取）
     * @return 成功删除的条目数量
     */
    @Delete("DELETE FROM shopping_cart WHERE user_id = #{userId} AND sku_id = #{skuId}")
    int deleteByUserIdAndSkuId(@Param("userId") Long userId, @Param("skuId") Integer skuId);

    /**
     * 清空指定用户的所有购物车条目
     * @param userId 用户ID
     * @return 成功清空的条目数量
     */
    @DeleteProvider(type = ShoppingCartSqlProvider.class, method = "clearCartByUserId")
    int clearCartByUserId(@Param("userId") Long userId);
}