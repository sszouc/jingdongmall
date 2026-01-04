package com.jingdong.mall.mapper;

import com.jingdong.mall.model.dto.request.ProductListRequest;
import com.jingdong.mall.model.entity.Product;
import com.jingdong.mall.provider.ProductSqlProvider;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProductMapper {

    // 原有方法保持不变
    @Select("SELECT * FROM product WHERE id = #{id} AND is_active = 1")
    Product selectById(@Param("id") Integer id);

    @Select("SELECT COUNT(*) FROM product WHERE id = #{id} AND is_active = 1")
    int existsById(@Param("id") Integer id);

    /**
     * 分页查询商品列表
     * @param request 查询条件
     * @return 商品列表
     */
    @SelectProvider(type = ProductSqlProvider.class, method = "selectProductList")
    List<Product> selectProductList(@Param("request") ProductListRequest request);

    /**
     * 统计符合条件的商品总数
     * @param request 查询条件
     * @return 商品总数
     */
    @SelectProvider(type = ProductSqlProvider.class, method = "countProductList")
    Long countProductList(@Param("request") ProductListRequest request);

    /**
     * 插入商品
     * @param product 商品实体
     * @return 插入行数
     */
    @InsertProvider(type = ProductSqlProvider.class, method = "insertProduct")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Product product);

    /**
     * 检查商品名称是否重复
     * @param name 商品名称
     * @return 重复数量
     */
    @Select("SELECT COUNT(*) FROM product WHERE name = #{name} AND is_deleted = 0")
    int countByName(@Param("name") String name);
}