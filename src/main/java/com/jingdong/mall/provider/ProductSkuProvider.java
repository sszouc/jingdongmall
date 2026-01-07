package com.jingdong.mall.provider;

import com.jingdong.mall.model.entity.ProductSku;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.jdbc.SQL;

public class ProductSkuProvider {

    /**
     * 插入SKU SQL
     */
    public String insertSku(ProductSku productSku) {
        SQL sql = new SQL();
        sql.INSERT_INTO("product_sku");

        // 必填字段
        sql.VALUES("product_id", "#{productId}");
        sql.VALUES("price", "#{price}");
        sql.VALUES("stock", "#{stock}");

        // 选填字段或带默认值的字段
        if (productSku.getSalesCount() != null) {
            sql.VALUES("sales_count", "#{salesCount}");
        } else {
            sql.VALUES("sales_count", "0");
        }

        sql.VALUES("os", "#{os}");
        sql.VALUES("cpu", "#{cpu}");
        sql.VALUES("ram", "#{ram}");
        sql.VALUES("storage", "#{storage}");
        sql.VALUES("gpu", "#{gpu}");
        sql.VALUES("ssd_capacity", "#{ssdCapacity}");
        sql.VALUES("gpu_chip", "#{gpuChip}");
        sql.VALUES("vram_capacity", "#{vramCapacity}");

        if (productSku.getIsActive() != null) {
            sql.VALUES("is_active", "#{isActive}");
        } else {
            sql.VALUES("is_active", "1");
        }

        sql.VALUES("created_time", "NOW()");
        sql.VALUES("updated_time", "NOW()");

        return sql.toString();
    }

    /**
     * 更新SKU SQL
     */
    public String updateSku(ProductSku productSku) {
        SQL sql = new SQL();
        sql.UPDATE("product_sku");

        // 更新字段，只更新非空字段
        if (productSku.getProductId() != null) {
            sql.SET("product_id = #{productId}");
        }

        if (productSku.getPrice() != null) {
            sql.SET("price = #{price}");
        }

        if (productSku.getStock() != null) {
            sql.SET("stock = #{stock}");
        }

        if (productSku.getSalesCount() != null) {
            sql.SET("sales_count = #{salesCount}");
        }

        if (productSku.getOs() != null) {
            sql.SET("os = #{os}");
        }

        if (productSku.getCpu() != null) {
            sql.SET("cpu = #{cpu}");
        }

        if (productSku.getRam() != null) {
            sql.SET("ram = #{ram}");
        }

        if (productSku.getStorage() != null) {
            sql.SET("storage = #{storage}");
        }

        if (productSku.getGpu() != null) {
            sql.SET("gpu = #{gpu}");
        }

        if (productSku.getSsdCapacity() != null) {
            sql.SET("ssd_capacity = #{ssdCapacity}");
        }

        if (productSku.getGpuChip() != null) {
            sql.SET("gpu_chip = #{gpuChip}");
        }

        if (productSku.getVramCapacity() != null) {
            sql.SET("vram_capacity = #{vramCapacity}");
        }

        if (productSku.getIsActive() != null) {
            sql.SET("is_active = #{isActive}");
        }

        // 更新时间
        sql.SET("updated_time = NOW()");

        sql.WHERE("id = #{id}");

        return sql.toString();
    }
}