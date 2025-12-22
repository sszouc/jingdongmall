package com.jingdong.mall.common.utils;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;

@Mapper
public interface UserUnbanMapper {
    @Update("UPDATE user SET " +
            "status = 1, " +
            "banned_start_time = NULL, " +
            "banned_end_time = NULL " +
            "WHERE status = 0 " +  // 只处理被封禁的用户
            "AND banned_end_time IS NOT NULL " +  // 有结束时间的封禁
            "AND banned_end_time <= #{currentTime}")  // 封禁已到期
    int unbanExpiredUsers(@Param("currentTime") LocalDateTime currentTime);
}
