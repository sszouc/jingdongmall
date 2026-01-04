package com.jingdong.mall.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Schema(description = "更新商品请求")
public class ProductUpdateRequest {

    @JsonProperty("category_id")
    @Schema(description = "分类ID，可为空")
    private Integer categoryId;

    @JsonProperty("name")
    @Size(max = 200)
    @Schema(description = "商品名称，更新时可选")
    private String name;

    @JsonProperty("description")
    @Schema(description = "商品描述")
    private String description;

    @JsonProperty("detail_html")
    @Schema(description = "商品详情HTML")
    private String detailHtml;

    @JsonProperty("main_images")
    @Schema(description = "主图列表")
    private List<String> mainImages;

    @JsonProperty("tags")
    @Schema(description = "标签列表")
    private List<String> tags;

    // 以下为可选的规格字段
    @JsonProperty("model")
    private String model;

    @JsonProperty("os")
    private String os;

    @JsonProperty("positioning")
    private String positioning;

    @JsonProperty("cpu_model")
    private String cpuModel;

    @JsonProperty("cpu_series")
    private String cpuSeries;

    @JsonProperty("max_turbo_freq")
    private String maxTurboFreq;

    @JsonProperty("cpu_chip")
    private String cpuChip;

    @JsonProperty("screen_size")
    private String screenSize;

    @JsonProperty("screen_ratio")
    private String screenRatio;

    @JsonProperty("resolution")
    private String resolution;

    @JsonProperty("color_gamut")
    private String colorGamut;

    @JsonProperty("refresh_rate")
    private String refreshRate;

    @JsonProperty("ram_type")
    private String ramType;

    @JsonProperty("ssd_type")
    private String ssdType;

    @JsonProperty("gpu_type")
    private String gpuType;

    @JsonProperty("vram_type")
    private String vramType;

    @JsonProperty("camera")
    private String camera;

    @JsonProperty("wifi")
    private String wifi;

    @JsonProperty("bluetooth")
    private String bluetooth;

    @JsonProperty("data_interfaces")
    private String dataInterfaces;

    @JsonProperty("video_interfaces")
    private String videoInterfaces;

    @JsonProperty("audio_interfaces")
    private String audioInterfaces;

    @JsonProperty("keyboard")
    private String keyboard;

    @JsonProperty("face_id")
    private String faceId;

    @JsonProperty("weight")
    private String weight;

    @JsonProperty("thickness")
    private String thickness;

    @JsonProperty("software")
    private String software;

    // Getters and setters

}

