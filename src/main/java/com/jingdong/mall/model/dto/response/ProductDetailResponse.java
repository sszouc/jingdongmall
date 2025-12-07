package com.jingdong.mall.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailResponse {
    private Integer id;
    private String name;
    private String desc;
    private String priceRange;
    private List<String> mainImages;
    private String detailHtml;
    private List<ProductSpec> specs;
    private List<ProductSkuResponse> skus;
    private ProductParams params;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductSpec {
        private String name;
        private List<String> values;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductSkuResponse {
        private Integer id;
        private Map<String, String> specs;
        private BigDecimal price;
        private Integer stock;
        private SkuDiffParams diffParams;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SkuDiffParams {
        private String ssdCapacity;
        private String gpuChip;
        private String vramCapacity;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductParams {
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
        private String ramCapacity;
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
    }
}