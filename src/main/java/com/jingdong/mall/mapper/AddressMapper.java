// mapper/AddressMapper.java
package com.jingdong.mall.mapper;

import com.jingdong.mall.model.entity.Address;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AddressMapper {

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
}