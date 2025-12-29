package com.jingdong.mall.mapper;

import com.jingdong.mall.model.dto.request.ProductAddRequest;
import com.jingdong.mall.model.entity.Product;
import com.jingdong.mall.provider.ProductAddSqlProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

/**
 * 商品新增相关Mapper
 */
@Mapper
public interface ProductAddMapper {

    /**
     * 新增商品
     */
    @InsertProvider(type = ProductAddSqlProvider.class, method = "insertProduct")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertProduct(Product product);

    /**
     * 检查商品名称是否已存在
     */
    @InsertProvider(type = ProductAddSqlProvider.class, method = "countByName")
    int countByName(String name);
}