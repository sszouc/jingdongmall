package com.jingdong.mall.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "更新SKU请求参数")
public class SkuUpdateRequest {

    @Min(value = 1, message = "产品ID必须大于0")
    @Schema(description = "产品ID", example = "1")
    @JsonProperty("product_id")
    private Integer productId;

    @DecimalMin(value = "0.01", message = "价格必须大于0")
    @Schema(description = "价格", example = "7999")
    private BigDecimal price;

    @Min(value = 0, message = "库存数量不能小于0")
    @Schema(description = "库存数量", example = "50")
    private Integer stock;

    @Min(value = 0, message = "销量不能小于0")
    @Schema(description = "销量", example = "10")
    @JsonProperty("sales_count")
    private Integer salesCount;

    @Schema(description = "操作系统", example = "Windows 11 专业版")
    private String os;

    @Schema(description = "处理器", example = "Intel® Core™ Ultra 7 165H")
    private String cpu;

    @Schema(description = "内存容量", example = "32GB")
    private String ram;

    @Schema(description = "存储容量", example = "1T SSD")
    private String storage;

    @Schema(description = "显卡", example = "RTX 5060")
    private String gpu;

    @Schema(description = "硬盘容量", example = "1T SSD")
    @JsonProperty("ssd_capacity")
    private String ssdCapacity;

    @Schema(description = "显卡芯片", example = "NVIDIA® GeForce RTX™ 5060")
    @JsonProperty("gpu_chip")
    private String gpuChip;

    @Schema(description = "显存容量", example = "6GB")
    @JsonProperty("vram_capacity")
    private String vramCapacity;

    @Schema(description = "是否激活：1-是，0-否", example = "1")
    @JsonProperty("is_active")
    private Integer isActive;
}