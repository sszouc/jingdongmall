// src/main/java/com/jingdong/mall/model/dto/request/ProductAddRequest.java
package com.jingdong.mall.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "新增商品请求参数")
public class ProductAddRequest {

    @Schema(description = "分类ID，如果为空表示未分类", example = "67")
    @JsonProperty("category_id")
    private Integer categoryId;

    @NotBlank(message = "商品名称不能为空")
    @Size(max = 200, message = "商品名称不能超过200个字符")
    @Schema(description = "商品名称", required = true, example = "宋雪")
    private String name;

    @Schema(description = "商品描述", example = "与想交半院导真。感住任整利。")
    private String description;

    @Schema(description = "商品详情HTML", example = "ut commodo fugiat")
    @JsonProperty("detail_html")
    private String detailHtml;

    @Schema(description = "主图URL数组", example = "[\"https://loremflickr.com/400/400?lock=1077822892061429\"]")
    @JsonProperty("main_images")
    private List<String> mainImages;

    @Schema(description = "商品标签数组", example = "[\"elit\", \"nulla\", \"ex aute\"]")
    private List<String> tags;

    @Schema(description = "产品型号", example = "ut tempor ipsum")
    private String model;

    @Schema(description = "操作系统", example = "laborum nostrud")
    private String os;

    @Schema(description = "产品定位", example = "ut commodo")
    private String positioning;

    @Schema(description = "CPU型号", example = "in")
    @JsonProperty("cpu_model")
    private String cpuModel;

    @Schema(description = "CPU系列", example = "laboris")
    @JsonProperty("cpu_series")
    private String cpuSeries;

    @Schema(description = "最高睿频", example = "sunt Excepteur enim anim nulla")
    @JsonProperty("max_turbo_freq")
    private String maxTurboFreq;

    @Schema(description = "CPU芯片", example = "0.17.190.3")
    @JsonProperty("cpu_chip")
    private String cpuChip;

    @Schema(description = "屏幕尺寸", example = "minim non laborum exercitation")
    @JsonProperty("screen_size")
    private String screenSize;

    @Schema(description = "显示比例", example = "consectetur commodo est anim officia")
    @JsonProperty("screen_ratio")
    private String screenRatio;

    @Schema(description = "分辨率", example = "ullamco laboris")
    private String resolution;

    @Schema(description = "色域", example = "culpa magna sit")
    @JsonProperty("color_gamut")
    private String colorGamut;

    @Schema(description = "刷新率", example = "nisi consectetur")
    @JsonProperty("refresh_rate")
    private String refreshRate;

    @Schema(description = "内存类型", example = "mollit aliquip")
    @JsonProperty("ram_type")
    private String ramType;

    @Schema(description = "硬盘类型", example = "in proident sed laboris et")
    @JsonProperty("ssd_type")
    private String ssdType;

    @Schema(description = "显卡类型", example = "quis officia occaecat et")
    @JsonProperty("gpu_type")
    private String gpuType;

    @Schema(description = "显存类型", example = "Excepteur minim sint tempor")
    @JsonProperty("vram_type")
    private String vramType;

    @Schema(description = "摄像头", example = "fugiat")
    private String camera;

    @Schema(description = "无线网卡", example = "sit")
    private String wifi;

    @Schema(description = "蓝牙", example = "fugiat officia elit")
    private String bluetooth;

    @Schema(description = "数据接口", example = "ut ut veniam")
    @JsonProperty("data_interfaces")
    private String dataInterfaces;

    @Schema(description = "视频接口", example = "30")
    @JsonProperty("video_interfaces")
    private String videoInterfaces;

    @Schema(description = "音频接口", example = "anim eiusmod irure Ut")
    @JsonProperty("audio_interfaces")
    private String audioInterfaces;

    @Schema(description = "键盘", example = "aliqua")
    private String keyboard;

    @Schema(description = "人脸识别", example = "71")
    @JsonProperty("face_id")
    private String faceId;

    @Schema(description = "重量", example = "nisi aute Excepteur non")
    private String weight;

    @Schema(description = "厚度", example = "in incididunt in")
    private String thickness;

    @Schema(description = "附带软件", example = "adipisicing")
    private String software;
}