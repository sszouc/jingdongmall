// model/dto/request/NoticeQueryRequest.java
package com.jingdong.mall.model.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Data
public class NoticeQueryRequest {

    @Min(value = 1, message = "页码最小为1")
    private Integer page = 1;

    @Min(value = 1, message = "每页数量最小为1")
    private Integer pageSize = 10;


    private String title;    // 公告标题模糊搜索
    private Integer type;    // 公告类型筛选
    private Integer isActive; // 状态筛选：1-启用，0-禁用

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    // 计算偏移量
    public Integer getOffset() {
        return (page - 1) * pageSize;
    }
}