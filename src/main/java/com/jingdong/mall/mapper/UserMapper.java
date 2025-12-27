// mapper/UserMapper.java
package com.jingdong.mall.mapper;

import com.jingdong.mall.model.dto.request.AdminUserListRequest;
import com.jingdong.mall.model.entity.User;
import com.jingdong.mall.provider.AdminSqlProvider;
import com.jingdong.mall.provider.UserSqlProvider;
import com.jingdong.mall.provider.UserStatisticsProvider;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.annotations.SelectProvider;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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

    // 分页查询用户列表（管理员用）
    @SelectProvider(type = UserSqlProvider.class, method = "selectUserListWithPage")
    List<User> selectUserList(@Param("request") AdminUserListRequest request);

    // 查询符合条件的用户总数
    @SelectProvider(type = UserSqlProvider.class, method = "countUserList")
    Long countUserList(@Param("request") AdminUserListRequest request);

    /**
     * 更新用户状态和封禁时间
     */
    @UpdateProvider(type = UserSqlProvider.class, method = "updateUserStatusAndBanTime")
    int updateUserStatusAndBanTime(@Param("id") Long id,
                                   @Param("status") Integer status,
                                   @Param("bannedStartTime") java.time.LocalDateTime bannedStartTime,
                                   @Param("bannedEndTime") java.time.LocalDateTime bannedEndTime);

    /**
     * 根据ID查询用户状态
     */
    @Select("SELECT id, status, role, banned_start_time, banned_end_time FROM user WHERE id = #{id}")
    User selectStatusById(Long id);

    /**
     * 获取用户统计信息
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 统计结果Map
     */
    @SelectProvider(type = UserStatisticsProvider.class, method = "getUserStatistics")
    Map<String, Object> getUserStatistics(@Param("startTime") String startTime,
                                          @Param("endTime") String endTime);

    /**
     * 检查是否存在具有管理员角色的用户
     */
    @SelectProvider(type = AdminSqlProvider.class, method = "checkAdminExists")
    int checkAdminExists(@Param("phone") String phone);

    /**
     * 更新用户角色为管理员
     */
    @UpdateProvider(type = AdminSqlProvider.class, method = "updateUserToAdmin")
    int updateUserToAdmin(@Param("userId") Long userId);

    /**
     * 统计用户名数量（用于生成唯一用户名）
     */
    @Select("SELECT COUNT(*) FROM user WHERE username = #{username}")
    int countByUsername(@Param("username") String username);

    /**
     * 查询管理员用户信息
     */
    @SelectProvider(type = AdminSqlProvider.class, method = "selectAdminUserById")
    User selectAdminUserById(@Param("userId") Long userId);

    /**
     * 删除管理员用户
     */
    @DeleteProvider(type = AdminSqlProvider.class, method = "deleteAdminUser")
    int deleteAdminUser(@Param("userId") Long userId);

    /**
     * 统计当前管理员数量（不包括超级管理员）
     */
    @Select("SELECT COUNT(*) FROM user WHERE role = 1 AND status = 1")
    int countAdminUsers();
}
