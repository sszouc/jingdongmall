package com.jingdong.mall.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.List;

/**
 * 新增商品请求参数
 */
@Data
public class ProductAddRequest {

    private Integer categoryId; // 分类ID，可为空

    @NotBlank(message = "商品名称不能为空")
    @Size(max = 200, message = "商品名称长度不能超过200字符")
    private String name; // 商品名称（必填）

    private String description; // 商品描述

    private String detailHtml; // 商品详情HTML

    private List<String> mainImages; // 商品主图URL数组

    private List<String> tags; // 商品标签数组

    private String model; // 型号

    private String os; // 操作系统

    private String positioning; // 定位

    private String cpuModel; // CPU型号

    private String cpuSeries; // CPU系列

    private String maxTurboFreq; // 最大睿频

    private String cpuChip; // CPU芯片

    private String screenSize; // 屏幕尺寸

    private String screenRatio; // 屏幕比例

    private String resolution; // 分辨率

    private String colorGamut; // 色域

    private String refreshRate; // 刷新率

    private String ramType; // 内存类型

    private String ssdType; // SSD类型

    private String gpuType; // GPU类型

    private String vramType; // 显存类型

    private String camera; // 摄像头

    private String wifi; // WiFi配置

    private String bluetooth; // 蓝牙版本

    private String dataInterfaces; // 数据接口

    private String videoInterfaces; // 视频接口

    private String audioInterfaces; // 音频接口

    private String keyboard; // 键盘配置

    private String faceId; // 人脸识别

    private String weight; // 重量

    private String thickness; // 厚度

    private String software; // 预装软件
}