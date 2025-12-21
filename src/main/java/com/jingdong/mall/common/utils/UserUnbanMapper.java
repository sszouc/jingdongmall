package com.jingdong.mall.common.utils;

import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;

@Mapper
public interface UserUnbanMapper {
    int unbanExpiredUsers(@Param("currentTime") LocalDateTime currentTime);
}
