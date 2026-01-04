package com.jingdong.mall.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Schema(description = "批量更新商品上下架状态请求")
public class ProductStatusUpdateRequest {

    @JsonProperty("ids")
    @NotEmpty(message = "商品ID数组不能为空")
    @Schema(description = "商品ID数组", required = true)
    private List<Integer> ids;

    @JsonProperty("status")
    @NotNull(message = "状态不能为空")
    @Min(value = 0)
    @Max(value = 1)
    @Schema(description = "状态：0-下架，1-上架", required = true)
    private Integer status;

}

