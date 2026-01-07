package com.jingdong.mall.model.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class SkuBatchStatusRequest {

    @NotEmpty(message = "ids不能为空")
    private List<Integer> ids;

    @NotNull(message = "is_active不能为空")
    @Min(value = 0, message = "is_active只能为0或1")
    @Max(value = 1, message = "is_active只能为0或1")
    @JsonProperty("is_active")
    @JsonAlias({"isActive", "is_active"})
    private Integer isActive;

}
