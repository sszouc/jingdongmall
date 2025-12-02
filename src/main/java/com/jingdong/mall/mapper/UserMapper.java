// mapper/UserMapper.java
package com.jingdong.mall.mapper;

import com.jingdong.mall.model.entity.User;
import org.apache.ibatis.annotations.*;

//不解释，参考spring三层架构
@Mapper
public interface UserMapper {

    @Insert("INSERT INTO user (phone, email, password, username, avatar, status, role) " +
            "VALUES (#{phone}, #{email}, #{password}, #{username}, #{avatar}, #{status}, #{role})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Select("SELECT COUNT(*) FROM user WHERE phone = #{phone}")
    int countByPhone(String phone);

    @Select("SELECT COUNT(*) FROM user WHERE email = #{email}")
    int countByEmail(String email);

    @Select("SELECT * FROM user WHERE phone = #{phone} OR email = #{email}")
    User selectByPhoneOrEmail(String account);
}