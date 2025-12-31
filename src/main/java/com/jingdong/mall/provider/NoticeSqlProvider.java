// mapper/provider/NoticeSqlProvider.java
package com.jingdong.mall.provider;

import com.jingdong.mall.model.dto.request.NoticeQueryRequest;
import com.jingdong.mall.model.entity.Notice;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.jdbc.SQL;

public class NoticeSqlProvider {

    // 生成查询公告列表的SQL - 修改SELECT和ORDER BY
    public String selectNoticeList(@Param("query") NoticeQueryRequest query) {
        return new SQL() {{
            SELECT("id, title, content, type, is_active, sort_order, created_time, updated_time"); // 添加sort_order
            FROM("notice");

            // 动态条件（不变）
            if (query.getTitle() != null && !query.getTitle().isEmpty()) {
                WHERE("title LIKE CONCAT('%', #{query.title}, '%')");
            }
            if (query.getType() != null) {
                WHERE("type = #{query.type}");
            }
            if (query.getIsActive() != null) {
                WHERE("is_active = #{query.isActive}");
            }
            if (query.getStartTime() != null) {
                WHERE("created_time >= #{query.startTime}");
            }
            if (query.getEndTime() != null) {
                WHERE("created_time <= #{query.endTime}");
            }

            // 修改排序规则：先按sort_order升序，再按创建时间倒序
            ORDER_BY("sort_order ASC, created_time DESC");
        }}.toString() + " LIMIT #{query.offset}, #{query.pageSize}";
    }

    // 生成统计公告总数的SQL
    public String countNoticeList(@Param("query") NoticeQueryRequest query) {
        return new SQL() {{
            SELECT("COUNT(*)");
            FROM("notice");

            // 动态条件
            if (query.getTitle() != null && !query.getTitle().isEmpty()) {
                WHERE("title LIKE CONCAT('%', #{query.title}, '%')");
            }
            if (query.getType() != null) {
                WHERE("type = #{query.type}");
            }
            if (query.getIsActive() != null) {
                WHERE("is_active = #{query.isActive}");
            }
            if (query.getStartTime() != null) {
                WHERE("created_time >= #{query.startTime}");
            }
            if (query.getEndTime() != null) {
                WHERE("created_time <= #{query.endTime}");
            }
        }}.toString();
    }

    // 生成更新公告的SQL - 添加sortOrder字段
    public String updateNotice(Notice notice) {
        return new SQL() {{
            UPDATE("notice");

            if (notice.getTitle() != null) {
                SET("title = #{title}");
            }
            if (notice.getContent() != null) {
                SET("content = #{content}");
            }
            if (notice.getType() != null) {
                SET("type = #{type}");
            }
            if (notice.getIsActive() != null) {
                SET("is_active = #{isActive}");
            }
            if (notice.getSortOrder() != null) { // 新增字段
                SET("sort_order = #{sortOrder}");
            }

            // 总是更新更新时间
            SET("updated_time = NOW()");

            WHERE("id = #{id}");
        }}.toString();
    }
}