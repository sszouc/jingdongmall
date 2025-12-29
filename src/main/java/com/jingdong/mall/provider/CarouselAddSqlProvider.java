package com.jingdong.mall.provider;

import com.jingdong.mall.model.entity.Carousel;
import org.apache.ibatis.jdbc.SQL;

/**
 * 轮播图新增动态SQL提供者
 */
public class CarouselAddSqlProvider {

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