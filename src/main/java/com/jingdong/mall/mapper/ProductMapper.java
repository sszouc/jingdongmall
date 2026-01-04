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

    /**
     * 更新商品（动态SQL，按非空字段更新）
     * @param product 商品实体
     * @return 更新行数
     */
    @UpdateProvider(type = ProductSqlProvider.class, method = "updateProduct")
    int updateProduct(Product product);

    /**
     * 更新时检查名称是否被其他商品占用
     */
    @Select("SELECT COUNT(*) FROM product WHERE name = #{name} AND id <> #{id} AND is_deleted = 0")
    int countByNameExceptId(@Param("name") String name, @Param("id") Integer id);

    /**
     * 根据ID删除商品
     */
    @Delete("DELETE FROM product WHERE id = #{id}")
    int deleteById(@Param("id") Integer id);

    /**
     * 根据ID批量更新 is_active 状态
     * @param ids 商品ID列表
     * @param status 状态 0/1
     * @return 更新的行数
     */
    @UpdateProvider(type = ProductSqlProvider.class, method = "batchUpdateStatus")
    int batchUpdateStatus(@Param("ids") java.util.List<Integer> ids, @Param("status") Integer status);

    /**
     * 统计给定ID列表中存在的商品数量
     */
    @SelectProvider(type = ProductSqlProvider.class, method = "countByIds")
    int countByIds(@Param("ids") java.util.List<Integer> ids);

}