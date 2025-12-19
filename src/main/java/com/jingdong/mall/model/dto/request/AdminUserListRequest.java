package com.jingdong.mall.model.dto.request;

import lombok.Data;

/**
 * 管理员用户列表查询请求参数
 */
@Data
public class AdminUserListRequest {

    /**
     * 搜索关键词（用户名/手机号/邮箱）
     */
    private String keyword;

    /**
     * 状态筛选：1正常，0禁用
     */
    private Integer status;

    /**
     * 当前页码，默认第1页
     */
    private Integer page = 1;

    /**
     * 每页数量，默认10条
     */
    private Integer pageSize = 10;

    /**
     * 获取偏移量（用于分页查询）
     */
    public Integer getOffset() {
        return (page - 1) * pageSize;
    }
}