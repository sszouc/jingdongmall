package com.jingdong.mall.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 用户状态更新请求参数（封禁/解禁）
 */
@Data
public class UserStatusUpdateRequest {

    /**
     * 操作类型：1-封禁，0-解禁
     */
    @NotNull(message = "操作类型不能为空")
    private Integer action;

    /**
     * 封禁时长（小时），解禁时可为空
     */
    private Integer endTime;
}