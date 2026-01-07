package com.jingdong.mall.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "新增SKU响应")
public class SkuAddResponse {

    @Schema(description = "SKU ID", example = "1")
    private Integer id;
}