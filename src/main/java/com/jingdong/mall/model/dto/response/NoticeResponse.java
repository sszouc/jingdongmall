// model/dto/response/NoticeResponse.java
package com.jingdong.mall.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NoticeResponse {
    private Long id;
    private String title;
    private String content;
    private Integer type;
    private String typeDesc; // 类型描述
    private Integer isActive;
    private String statusDesc; // 状态描述
    private Integer sortOrder; // 新增字段

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}