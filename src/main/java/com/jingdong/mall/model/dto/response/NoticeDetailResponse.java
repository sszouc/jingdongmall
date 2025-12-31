// model/dto/response/NoticeDetailResponse.java
package com.jingdong.mall.model.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NoticeDetailResponse {
    private Long id;
    private String title;
    private String content;
    private Integer type;
    private Integer isActive;
    private Integer sortOrder; // 新增字段
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}