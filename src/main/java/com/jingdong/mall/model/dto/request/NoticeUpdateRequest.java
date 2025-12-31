// model/dto/request/NoticeUpdateRequest.java
package com.jingdong.mall.model.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NoticeUpdateRequest {

    @Size(min = 1, max = 200, message = "标题长度1-200字符")
    private String title;

    private String content;

    private Integer type;

    private Integer isActive;

    private Integer sortOrder; // 新增字段
}