package com.jingdong.mall.model.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Product {
    private Integer id;
    private Integer categoryId;
    private String name;
    private String description;
    private String detailHtml;
    private String mainImages; // JSON格式存储
    private String tags; // JSON格式存储
    private String model;
    private String os;
    private String positioning;
    private String cpuModel;
    private String cpuSeries;
    private String maxTurboFreq;
    private String cpuChip;
    private String screenSize;
    private String screenRatio;
    private String resolution;
    private String colorGamut;
    private String refreshRate;
    private String ramType;
    private String ssdType;
    private String gpuType;
    private String vramType;
    private String camera;
    private String wifi;
    private String bluetooth;
    private String dataInterfaces;
    private String videoInterfaces;
    private String audioInterfaces;
    private String keyboard;
    private String faceId;
    private String weight;
    private String thickness;
    private String software;
    private Integer isActive;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}