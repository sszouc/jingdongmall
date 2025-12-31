// model/entity/Notice.java
package com.jingdong.mall.model.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Notice {
    private Long id;
    private String title;
    private String content;
    private Integer type;
    private Boolean isActive;
    private Integer sortOrder; // 新增字段
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}