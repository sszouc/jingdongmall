package com.jingdong.mall.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.util.List;

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

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDetailHtml(String detailHtml) {
        this.detailHtml = detailHtml;
    }

    public void setMainImages(List<String> mainImages) {
        this.mainImages = mainImages;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public void setPositioning(String positioning) {
        this.positioning = positioning;
    }

    public void setCpuModel(String cpuModel) {
        this.cpuModel = cpuModel;
    }

    public void setCpuSeries(String cpuSeries) {
        this.cpuSeries = cpuSeries;
    }

    public void setMaxTurboFreq(String maxTurboFreq) {
        this.maxTurboFreq = maxTurboFreq;
    }

    public void setCpuChip(String cpuChip) {
        this.cpuChip = cpuChip;
    }

    public void setScreenSize(String screenSize) {
        this.screenSize = screenSize;
    }

    public void setScreenRatio(String screenRatio) {
        this.screenRatio = screenRatio;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public void setColorGamut(String colorGamut) {
        this.colorGamut = colorGamut;
    }

    public void setRefreshRate(String refreshRate) {
        this.refreshRate = refreshRate;
    }

    public void setRamType(String ramType) {
        this.ramType = ramType;
    }

    public void setSsdType(String ssdType) {
        this.ssdType = ssdType;
    }

    public void setGpuType(String gpuType) {
        this.gpuType = gpuType;
    }

    public void setVramType(String vramType) {
        this.vramType = vramType;
    }

    public void setCamera(String camera) {
        this.camera = camera;
    }

    public void setWifi(String wifi) {
        this.wifi = wifi;
    }

    public void setBluetooth(String bluetooth) {
        this.bluetooth = bluetooth;
    }

    public void setDataInterfaces(String dataInterfaces) {
        this.dataInterfaces = dataInterfaces;
    }

    public void setVideoInterfaces(String videoInterfaces) {
        this.videoInterfaces = videoInterfaces;
    }

    public void setAudioInterfaces(String audioInterfaces) {
        this.audioInterfaces = audioInterfaces;
    }

    public void setKeyboard(String keyboard) {
        this.keyboard = keyboard;
    }

    public void setFaceId(String faceId) {
        this.faceId = faceId;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public void setThickness(String thickness) {
        this.thickness = thickness;
    }

    public void setSoftware(String software) {
        this.software = software;
    }
}

