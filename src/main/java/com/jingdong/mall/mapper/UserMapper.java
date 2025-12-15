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

    @Select("SELECT COUNT(*) FROM user WHERE phone = #{phone} AND id != #{excludeId}")
    int countByPhoneExcludingUser(@Param("phone") String phone, @Param("excludeId") Long excludeId);

    @Select("SELECT COUNT(*) FROM user WHERE email = #{email} AND id != #{excludeId}")
    int countByEmailExcludingUser(@Param("email") String email, @Param("excludeId") Long excludeId);

    @Update("UPDATE user SET password = #{password}, updated_time = #{updatedTime} WHERE id = #{id}")
    int updatePasswordById(@Param("id") Long id,
                           @Param("password") String password,
                           @Param("updatedTime") LocalDateTime updatedTime);

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

    // 新增：更新头像专用方法
    @Update("UPDATE user SET avatar = #{avatar}, updated_time = #{updatedTime} WHERE id = #{userId}")
    int updateAvatar(@Param("userId") Long userId,
                     @Param("avatar") String avatar,
                     @Param("updatedTime") LocalDateTime updatedTime);

    @Delete("DELETE FROM user WHERE id = #{id}")
    int deleteById(Long id);

    @Select("SELECT * FROM user WHERE id = #{id}")
    User selectById(Long id);
}
