// src/main/java/com/jingdong/mall/model/dto/response/ProductAddResponse.java
package com.jingdong.mall.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "新增商品响应")
public class ProductAddResponse {

    @Schema(description = "数据库自动分配的商品ID", example = "123")
    private Integer id;
}