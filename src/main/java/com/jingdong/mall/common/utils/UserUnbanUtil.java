package com.jingdong.mall.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@Slf4j
@EnableScheduling
public class UserUnbanUtil {

    private final UserUnbanMapper userUnbanMapper;

    public UserUnbanUtil(UserUnbanMapper userUnbanMapper) {
        this.userUnbanMapper = userUnbanMapper;
    }

    /**
     * 每小时执行一次，解除到期的封禁
     * 秒 分 时 日 月 周
     * 0 0 * * * ? 表示每小时的第0分钟第0秒执行
     */
    @Scheduled(cron = "0 0 * * * ?")  // 每小时整点执行
    @Transactional
    public void unbanExpiredUsers() {
        LocalDateTime now = LocalDateTime.now();
        log.info("开始执行封禁解除定时任务，当前时间：{}", now);

        // 查询并解除到期的封禁
        int count = userUnbanMapper.unbanExpiredUsers(now);

        if (count > 0) {
            log.info("成功解除 {} 个用户的封禁", count);
        } else {
            log.debug("没有需要解除封禁的用户");
        }
    }
}