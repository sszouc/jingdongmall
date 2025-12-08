package com.jingdong.mall.mapper;

import com.jingdong.mall.model.entity.ProductSku;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface ProductSkuMapper {

    @Select("SELECT * FROM product_sku WHERE product_id = #{productId} AND is_active = 1 ORDER BY price")
    @Results({
            @Result(property = "productId", column = "product_id"),
            @Result(property = "skuCode", column = "sku_code"),
            @Result(property = "salesCount", column = "sales_count"),
            @Result(property = "ssdCapacity", column = "ssd_capacity"),
            @Result(property = "gpuChip", column = "gpu_chip"),
            @Result(property = "vramCapacity", column = "vram_capacity"),
            @Result(property = "isActive", column = "is_active"),
            @Result(property = "createdTime", column = "created_time"),
            @Result(property = "updatedTime", column = "updated_time")
    })
    List<ProductSku> selectByProductId(@Param("productId") Integer productId);

    @Select("SELECT MIN(price) FROM product_sku WHERE product_id = #{productId} AND is_active = 1")
    BigDecimal selectMinPrice(@Param("productId") Integer productId);

    @Select("SELECT MAX(price) FROM product_sku WHERE product_id = #{productId} AND is_active = 1")
    BigDecimal selectMaxPrice(@Param("productId") Integer productId);

    // 新增：根据skuId查询单个SKU
    @Select("SELECT * FROM product_sku WHERE id = #{skuId}")
    ProductSku selectBySkuId(Integer skuId);
}