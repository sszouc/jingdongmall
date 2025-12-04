package com.jingdong.mall.mapper;

import com.jingdong.mall.model.entity.Address;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AddressMapper {

    // 原有方法保持不变
    @Select("SELECT id, user_id, name, phone, province, city, district, detail, postal_code, " +
            "is_default, status, created_time, updated_time " +
            "FROM user_address " +
            "WHERE user_id = #{userId} AND status = 1 " +
            "ORDER BY is_default DESC, updated_time DESC")
    @Results({
            @Result(property = "postalCode", column = "postal_code"),
            @Result(property = "isDefault", column = "is_default"),
            @Result(property = "createdTime", column = "created_time"),
            @Result(property = "updatedTime", column = "updated_time")
    })
    List<Address> selectByUserId(@Param("userId") Long userId);

    @Select("SELECT COUNT(*) FROM user_address WHERE user_id = #{userId} AND status = 1")
    int countByUserId(@Param("userId") Long userId);

    // 新增方法：新增地址
    @Insert("INSERT INTO user_address (user_id, name, phone, province, city, district, detail, postal_code, is_default, status, created_time, updated_time) " +
            "VALUES (#{userId}, #{name}, #{phone}, #{province}, #{city}, #{district}, #{detail}, #{postalCode}, #{isDefault}, 1, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Address address);

    // 新增方法：修改地址
    @Update("UPDATE user_address SET " +
            "name = #{name}, " +
            "phone = #{phone}, " +
            "province = #{province}, " +
            "city = #{city}, " +
            "district = #{district}, " +
            "detail = #{detail}, " +
            "postal_code = #{postalCode}, " +
            "is_default = #{isDefault}, " +
            "updated_time = NOW() " +
            "WHERE id = #{id} AND user_id = #{userId} AND status = 1")
    int update(Address address);

    // 新增方法：删除地址（逻辑删除）
    @Update("UPDATE user_address SET status = 0, updated_time = NOW() " +
            "WHERE id = #{id} AND user_id = #{userId}")
    int deleteById(@Param("id") Integer id, @Param("userId") Long userId);

    // 新增方法：查询用户默认地址数量
    @Select("SELECT COUNT(*) FROM user_address WHERE user_id = #{userId} AND is_default = 1 AND status = 1")
    int countDefaultByUserId(@Param("userId") Long userId);

    // 新增方法：取消用户所有默认地址
    @Update("UPDATE user_address SET is_default = 0, updated_time = NOW() " +
            "WHERE user_id = #{userId} AND is_default = 1 AND status = 1")
    int cancelAllDefault(@Param("userId") Long userId);
}