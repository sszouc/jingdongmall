// mapper/NoticeMapper.java
package com.jingdong.mall.mapper;

import com.jingdong.mall.model.dto.request.NoticeQueryRequest;
import com.jingdong.mall.model.entity.Notice;
import com.jingdong.mall.provider.NoticeSqlProvider;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface NoticeMapper {

    // 分页查询公告列表
    @SelectProvider(type = NoticeSqlProvider.class, method = "selectNoticeList")
    List<Notice> selectNoticeList(@Param("query") NoticeQueryRequest query);

    // 统计公告总数
    @SelectProvider(type = NoticeSqlProvider.class, method = "countNoticeList")
    Long countNoticeList(@Param("query") NoticeQueryRequest query);

    // 根据ID查询公告 - 修改SELECT语句
    @Select("SELECT id, title, content, type, is_active, sort_order, created_time, updated_time " +
            "FROM notice " +
            "WHERE id = #{id}")
    @Results({
            @Result(property = "isActive", column = "is_active"),
            @Result(property = "sortOrder", column = "sort_order"), // 新增映射
            @Result(property = "createdTime", column = "created_time"),
            @Result(property = "updatedTime", column = "updated_time")
    })
    Notice selectById(@Param("id") Long id);


    // 插入公告 - 修改INSERT语句
    @Insert("INSERT INTO notice (title, content, type, is_active, sort_order) " +
            "VALUES (#{title}, #{content}, #{type}, #{isActive}, #{sortOrder})") // 添加sortOrder
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Notice notice);

    // 更新公告
    @UpdateProvider(type = NoticeSqlProvider.class, method = "updateNotice")
    int update(Notice notice);

    // 删除公告（物理删除）
    @Delete("DELETE FROM notice WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    // 更新公告状态
    @Update("UPDATE notice SET is_active = #{isActive}, updated_time = NOW() " +
            "WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("isActive") Integer isActive);
}