package com.jingdong.mall.model.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 创建优惠券请求
 */
@Data
public class CouponCreateRequest {

    private Long id; // 用于接收生成的主键

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotNull
    @Min(1)
    @Max(2)
    private Integer type; // 1-满减，2-折扣

    @NotNull
    @DecimalMin("0")
    private BigDecimal value; // 金额或折扣（如9.5表示95折）

    @NotNull
    @DecimalMin("0")
    private BigDecimal minSpend;

    @NotBlank
    private String startTime; // 格式 yyyy-MM-dd HH:mm:ss

    @NotBlank
    private String endTime; // 格式 yyyy-MM-dd HH:mm:ss

    private Integer totalCount = 0;

    private Integer status = 1; // 1启用，0禁用
}
