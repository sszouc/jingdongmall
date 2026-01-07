package com.jingdong.mall.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "新增SKU请求参数")
public class SkuAddRequest {

    @NotNull(message = "产品ID不能为空")
    @Min(value = 1, message = "产品ID必须大于0")
    @Schema(description = "产品ID", required = true, example = "1")
    @JsonProperty("product_id")
    private Integer productId;

    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.01", message = "价格必须大于0")
    @Schema(description = "价格", required = true, example = "8999")
    private BigDecimal price;

    @NotNull(message = "库存数量不能为空")
    @Min(value = 0, message = "库存数量不能小于0")
    @Schema(description = "库存数量", required = true, example = "100")
    private Integer stock;

    @Min(value = 0, message = "销量不能小于0")
    @Schema(description = "销量", example = "0")
    @JsonProperty("sales_count")
    private Integer salesCount;

    @NotBlank(message = "操作系统不能为空")
    @Schema(description = "操作系统", required = true, example = "Windows 11 家庭中文版")
    private String os;

    @NotBlank(message = "处理器不能为空")
    @Schema(description = "处理器", required = true, example = "Intel® Core™ Ultra 9 275HX")
    private String cpu;

    @NotBlank(message = "内存容量不能为空")
    @Schema(description = "内存容量", required = true, example = "64GB")
    private String ram;

    @NotBlank(message = "存储容量不能为空")
    @Schema(description = "存储容量", required = true, example = "2T SSD")
    private String storage;

    @NotBlank(message = "显卡不能为空")
    @Schema(description = "显卡", required = true, example = "RTX 5070")
    private String gpu;

    @NotBlank(message = "硬盘容量不能为空")
    @Schema(description = "硬盘容量", required = true, example = "2T(1TB+1TB) SSD")
    @JsonProperty("ssd_capacity")
    private String ssdCapacity;

    @NotBlank(message = "显卡芯片不能为空")
    @Schema(description = "显卡芯片", required = true, example = "NVIDIA® GeForce RTX™ 5070")
    @JsonProperty("gpu_chip")
    private String gpuChip;

    @NotBlank(message = "显存容量不能为空")
    @Schema(description = "显存容量", required = true, example = "8GB")
    @JsonProperty("vram_capacity")
    private String vramCapacity;

    @Schema(description = "是否激活：1-是，0-否", example = "1")
    @JsonProperty("is_active")
    private Integer isActive;
}