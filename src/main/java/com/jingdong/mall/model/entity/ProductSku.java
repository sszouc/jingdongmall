package com.jingdong.mall.model.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductSku {
    private Integer id;
    private Integer productId;
    private String skuCode;
    private BigDecimal price;
    private Integer stock;
    private Integer salesCount;
    private String os;
    private String cpu;
    private String ram;
    private String storage;
    private String gpu;
    private String ssdCapacity;
    private String gpuChip;
    private String vramCapacity;
    private Integer isActive;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}