package com.jingdong.mall.mapper;

import com.jingdong.mall.model.entity.ProductSku;
import com.jingdong.mall.provider.ProductSkuProvider;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface ProductSkuMapper {

    // 原有方法保持不变
    @Select("SELECT * FROM product_sku WHERE product_id = #{productId} AND is_active = 1 ORDER BY price")
    List<ProductSku> selectByProductId(@Param("productId") Integer productId);

    @Select("SELECT MIN(price) FROM product_sku WHERE product_id = #{productId} AND is_active = 1")
    BigDecimal selectMinPrice(@Param("productId") Integer productId);

    @Select("SELECT MAX(price) FROM product_sku WHERE product_id = #{productId} AND is_active = 1")
    BigDecimal selectMaxPrice(@Param("productId") Integer productId);

    @Select("SELECT * FROM product_sku WHERE id = #{skuId}")
    ProductSku selectBySkuId(Integer skuId);

    /**
     * 插入SKU
     * @param productSku SKU实体
     * @return 插入行数
     */
    @InsertProvider(type = ProductSkuProvider.class, method = "insertSku")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ProductSku productSku);

    /**
     * 更新SKU
     * @param productSku SKU实体
     * @return 更新行数
     */
    @UpdateProvider(type = ProductSkuProvider.class, method = "updateSku")
    int update(ProductSku productSku);

    /**
     * 删除SKU
     * @param skuId SKU ID
     * @return 删除行数
     */
    @Delete("DELETE FROM product_sku WHERE id = #{skuId}")
    int delete(Integer skuId);

    /**
     * 检查产品是否存在
     * @param productId 产品ID
     * @return 存在数量
     */
    @Select("SELECT COUNT(*) FROM product WHERE id = #{productId} AND is_active = 1 AND is_deleted = 0")
    int countProductExists(@Param("productId") Integer productId);

    /**
     * 检查相同规格的SKU是否已存在（排除自身）
     * @param skuId 排除的SKU ID
     * @param productId 产品ID
     * @param os 操作系统
     * @param cpu 处理器
     * @param ram 内存容量
     * @param storage 存储容量
     * @param gpu 显卡
     * @return 存在数量
     */
    @Select("SELECT COUNT(*) FROM product_sku WHERE product_id = #{productId} " +
            "AND os = #{os} AND cpu = #{cpu} AND ram = #{ram} " +
            "AND storage = #{storage} AND gpu = #{gpu} " +
            "AND id != #{skuId} AND is_active = 1")
    int countDuplicateSkuExcludeSelf(
            @Param("skuId") Integer skuId,
            @Param("productId") Integer productId,
            @Param("os") String os,
            @Param("cpu") String cpu,
            @Param("ram") String ram,
            @Param("storage") String storage,
            @Param("gpu") String gpu);

    /**
     * 检查SKU是否存在
     * @param skuId SKU ID
     * @return 存在数量
     */
    @Select("SELECT COUNT(*) FROM product_sku WHERE id = #{skuId}")
    int countById(@Param("skuId") Integer skuId);

    /**
     * 批量更新SKU状态
     * @param ids SKU ID列表
     * @param isActive 激活状态
     * @return 更新行数
     */
    @UpdateProvider(type = ProductSkuProvider.class, method = "batchUpdateStatus")
    int batchUpdateStatus(@Param("ids") List<Integer> ids, @Param("isActive") Integer isActive);

    /**
     * 统计给定ids中存在的SKU数量
     * @param ids SKU ID列表
     * @return 存在数量
     */
    @SelectProvider(type = ProductSkuProvider.class, method = "countByIds")
    int countByIds(@Param("ids") List<Integer> ids);
}