package com.jingdong.mall.provider;

import org.apache.ibatis.jdbc.SQL;

/**
 * 轮播图动态SQL提供者
 */
public class CarouselSqlProvider {
    /**
     * 构建查询启用轮播图的SQL
     */
    public String selectActiveCarousels() {
        return new SQL() {{
            SELECT("id, imgUrl, linkUrl, sortOrder, isActive");
            FROM("carousel");
            WHERE("isActive = 1"); // 只查询启用状态的轮播图
            ORDER_BY("sortOrder ASC"); // 按排序字段升序排列
        }}.toString();
    }
}