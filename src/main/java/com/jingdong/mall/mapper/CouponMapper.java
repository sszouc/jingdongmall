package com.jingdong.mall.mapper;

import com.jingdong.mall.model.dto.request.CouponListRequest;
import com.jingdong.mall.model.dto.request.CouponUpdateRequest;
import com.jingdong.mall.model.entity.Coupon;
import com.jingdong.mall.model.dto.response.CouponListItemResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CouponMapper {

    /**
     * 分页查询优惠券列表
     */
    @SelectProvider(type = com.jingdong.mall.provider.CouponSqlProvider.class, method = "selectCouponList")
    List<CouponListItemResponse> selectCouponList(@Param("request") CouponListRequest request);

    /**
     * 统计符合条件的优惠券总数
     */
    @SelectProvider(type = com.jingdong.mall.provider.CouponSqlProvider.class, method = "countCouponList")
    Integer countCouponList(@Param("request") CouponListRequest request);

    /**
     * 根据ID查询优惠券
     */
    @Select("SELECT id, name, type, value, min_spend as minSpend, start_time as startTime, end_time as endTime, status, total_count as totalCount, used_count as usedCount, created_time as createTime FROM coupon WHERE id = #{id}")
    CouponListItemResponse selectById(Long id);

    /**
     * 插入新优惠券
     */
    @Insert("INSERT INTO coupon (name, type, value, min_spend, start_time, end_time, total_count, used_count, status, created_time, updated_time) VALUES (#{name}, #{type}, #{value}, #{minSpend}, #{startTime}, #{endTime}, #{totalCount}, 0, #{status}, NOW(), NOW())")
    @org.apache.ibatis.annotations.Options(useGeneratedKeys = true, keyProperty = "id")
    int insertCoupon(Coupon coupon);

    /**
     * 更新优惠券（动态SQL）
     */
    @UpdateProvider(type = com.jingdong.mall.provider.CouponSqlProvider.class, method = "updateCoupon")
    int updateCoupon(@Param("id") Long id, @Param("request") CouponUpdateRequest request);

    /**
     * 删除优惠券
     */
    @Delete("DELETE FROM coupon WHERE id = #{id}")
    int deleteCoupon(@Param("id") Long id);

    /**
     * 检查同名优惠券（排除指定ID，用于更新时检查重复）
     */
    @Select("SELECT COUNT(*) FROM coupon WHERE name = #{name} AND id != #{excludeId}")
    int countByNameExcludeId(@Param("name") String name, @Param("excludeId") Long excludeId);
}
