package com.jingdong.mall.mapper;

import com.jingdong.mall.model.entity.ProductSku;
import com.jingdong.mall.provider.ProductSkuProvider;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface ProductSkuMapper {

    @Select("SELECT * FROM product_sku WHERE product_id = #{productId} AND is_active = 1 ORDER BY price")
    List<ProductSku> selectByProductId(@Param("productId") Integer productId);

    @Select("SELECT MIN(price) FROM product_sku WHERE product_id = #{productId} AND is_active = 1")
    BigDecimal selectMinPrice(@Param("productId") Integer productId);

    @Select("SELECT MAX(price) FROM product_sku WHERE product_id = #{productId} AND is_active = 1")
    BigDecimal selectMaxPrice(@Param("productId") Integer productId);

    // 新增：根据skuId查询单个SKU
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
     * 检查产品是否存在
     * @param productId 产品ID
     * @return 存在数量
     */
    @Select("SELECT COUNT(*) FROM product WHERE id = #{productId} AND is_active = 1 AND is_deleted = 0")
    int countProductExists(@Param("productId") Integer productId);

    /**
     * 检查相同规格的SKU是否已存在
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
            "AND storage = #{storage} AND gpu = #{gpu} AND is_active = 1")
    int countDuplicateSku(
            @Param("productId") Integer productId,
            @Param("os") String os,
            @Param("cpu") String cpu,
            @Param("ram") String ram,
            @Param("storage") String storage,
            @Param("gpu") String gpu);
}