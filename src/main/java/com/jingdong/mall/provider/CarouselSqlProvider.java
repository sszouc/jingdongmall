package com.jingdong.mall.provider;

import com.jingdong.mall.model.entity.Carousel;
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
            SELECT("id, img_url, link_url, sort_order, is_active");
            FROM("carousel");
            WHERE("is_active = 1"); // 只查询启用状态的轮播图
            ORDER_BY("sort_order ASC"); // 按排序字段升序排列
        }}.toString();
    }
    /**
     * 构建删除轮播图的SQL
     */
    public String deleteCarouselById(Long id) {
        return new SQL() {{
            DELETE_FROM("carousel");
            WHERE("id = #{id}");
        }}.toString();
    }

    /**
     * 构建检查轮播图是否存在的SQL
     */
    public String existsById(Long id) {
        return new SQL() {{
            SELECT("COUNT(*)");
            FROM("carousel");
            WHERE("id = #{id}");
        }}.toString();
    }

    /**
     * 构建新增轮播图SQL
     */
    public String insertCarousel(Carousel carousel) {
        return new SQL() {{
            INSERT_INTO("carousel");
            VALUES("img_url", "#{imgUrl}");

            // 动态设置可选字段
            if (carousel.getLinkUrl() != null) {
                VALUES("link_url", "#{linkUrl}");
            }
            VALUES("sort_order", "#{sortOrder}");
            VALUES("is_active", "#{isActive}");
            VALUES("created_time", "NOW()");
            VALUES("updated_time", "NOW()");
        }}.toString();
    }
}