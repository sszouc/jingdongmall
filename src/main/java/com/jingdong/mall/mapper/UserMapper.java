// mapper/UserMapper.java
package com.jingdong.mall.mapper;

import com.jingdong.mall.model.entity.User;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;

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

    @Update("UPDATE user SET password = #{password}, updated_time = #{updatedTime} WHERE id = #{id}")
    int updatePasswordById(@Param("id") Long id,
                           @Param("password") String password,
                           @Param("updatedTime") LocalDateTime updatedTime);

    //根据ID查询用户
    @Select("SELECT * FROM user WHERE id = #{id}")
    User selectById(@Param("id") Integer id);

    // 更新用户信息
    @Update("UPDATE user SET " +
            "username = #{username}, " +
            "email = #{email}, " +
            "phone = #{phone}, " +
            "avatar = #{avatar}, " +
            "gender = #{gender}, " +
            "birthday = #{birthday}, " +
            "updated_time = #{updatedTime} " +
            "WHERE id = #{id}")
    int updateUserInfo(User user);
}
