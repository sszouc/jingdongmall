// model/dto/request/NoticeCreateRequest.java
package com.jingdong.mall.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NoticeCreateRequest {

    @NotBlank(message = "公告标题不能为空")
    @Size(min = 1, max = 200, message = "标题长度1-200字符")
    private String title;

    @NotBlank(message = "公告内容不能为空")
    private String content;

    private Integer type = 1; // 默认为系统公告

    private Integer isActive = 1; // 默认启用

    private Integer sortOrder = 0; // 新增字段，默认排序为0
}