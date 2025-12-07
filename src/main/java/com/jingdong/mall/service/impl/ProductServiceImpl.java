package com.jingdong.mall.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jingdong.mall.common.exception.BusinessException;
import com.jingdong.mall.common.exception.ErrorCode;
import com.jingdong.mall.mapper.ProductMapper;
import com.jingdong.mall.mapper.ProductSkuMapper;
import com.jingdong.mall.model.dto.response.ProductDetailResponse;
import com.jingdong.mall.model.entity.Product;
import com.jingdong.mall.model.entity.ProductSku;
import com.jingdong.mall.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductSkuMapper productSkuMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public ProductDetailResponse getProductDetail(Integer productId) {
        // 1. 验证商品ID
        if (productId == null || productId <= 0) {
            throw new BusinessException("商品ID不能为空");
        }

        // 2. 查询商品基本信息
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_EXIST);
        }

        // 3. 查询商品SKU列表
        List<ProductSku> skus = productSkuMapper.selectByProductId(productId);
        if (skus.isEmpty()) {
            throw new BusinessException("商品暂无库存");
        }

        // 4. 构建商品详情响应
        return buildProductDetailResponse(product, skus);
    }

    private ProductDetailResponse buildProductDetailResponse(Product product, List<ProductSku> skus) {
        ProductDetailResponse response = new ProductDetailResponse();

        // 基本信息
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDesc(product.getDescription());
        response.setDetailHtml(product.getDetailHtml());

        // 价格范围
        BigDecimal minPrice = productSkuMapper.selectMinPrice(product.getId());
        BigDecimal maxPrice = productSkuMapper.selectMaxPrice(product.getId());
        response.setPriceRange(String.format("¥%.0f - ¥%.0f", minPrice, maxPrice));

        // 主图列表
        try {
            List<String> mainImages = objectMapper.readValue(
                    product.getMainImages(),
                    new TypeReference<List<String>>() {}
            );
            response.setMainImages(mainImages);
        } catch (Exception e) {
            log.warn("解析商品主图失败: productId={}", product.getId());
            response.setMainImages(new ArrayList<>());
        }

        // 规格列表
        response.setSpecs(buildProductSpecs(skus));

        // SKU列表
        response.setSkus(buildSkuResponses(skus));

        // 商品参数
        response.setParams(buildProductParams(product));

        return response;
    }

    private List<ProductDetailResponse.ProductSpec> buildProductSpecs(List<ProductSku> skus) {
        // 从SKU中提取所有可能的规格
        Map<String, Set<String>> specMap = new LinkedHashMap<>();

        // 定义规格名称映射
        Map<String, String> specNameMap = new HashMap<>();
        specNameMap.put("os", "操作系统");
        specNameMap.put("cpu", "处理器");
        specNameMap.put("ram", "内存容量");
        specNameMap.put("storage", "存储容量");
        specNameMap.put("gpu", "显卡");

        for (ProductSku sku : skus) {
            addSpecValue(specMap, "os", sku.getOs());
            addSpecValue(specMap, "cpu", sku.getCpu());
            addSpecValue(specMap, "ram", sku.getRam());
            addSpecValue(specMap, "storage", sku.getStorage());
            addSpecValue(specMap, "gpu", sku.getGpu());
        }

        // 转换为ProductSpec列表
        return specMap.entrySet().stream()
                .map(entry -> {
                    ProductDetailResponse.ProductSpec spec = new ProductDetailResponse.ProductSpec();
                    spec.setName(specNameMap.getOrDefault(entry.getKey(), entry.getKey()));
                    spec.setValues(new ArrayList<>(entry.getValue()));
                    return spec;
                })
                .collect(Collectors.toList());
    }

    private void addSpecValue(Map<String, Set<String>> specMap, String key, String value) {
        if (value != null && !value.trim().isEmpty()) {
            specMap.computeIfAbsent(key, k -> new LinkedHashSet<>()).add(value);
        }
    }

    private List<ProductDetailResponse.ProductSkuResponse> buildSkuResponses(List<ProductSku> skus) {
        return skus.stream().map(sku -> {
            ProductDetailResponse.ProductSkuResponse skuResponse = new ProductDetailResponse.ProductSkuResponse();
            skuResponse.setId(sku.getId());
            skuResponse.setPrice(sku.getPrice());
            skuResponse.setStock(sku.getStock());

            // SKU规格
            Map<String, String> specs = new HashMap<>();
            if (sku.getOs() != null) specs.put("os", sku.getOs());
            if (sku.getCpu() != null) specs.put("cpu", sku.getCpu());
            if (sku.getRam() != null) specs.put("ram", sku.getRam());
            if (sku.getStorage() != null) specs.put("storage", sku.getStorage());
            if (sku.getGpu() != null) specs.put("gpu", sku.getGpu());
            skuResponse.setSpecs(specs);

            // SKU特有参数
            ProductDetailResponse.SkuDiffParams diffParams = new ProductDetailResponse.SkuDiffParams();
            diffParams.setSsdCapacity(sku.getSsdCapacity());
            diffParams.setGpuChip(sku.getGpuChip());
            diffParams.setVramCapacity(sku.getVramCapacity());
            skuResponse.setDiffParams(diffParams);

            return skuResponse;
        }).collect(Collectors.toList());
    }

    private ProductDetailResponse.ProductParams buildProductParams(Product product) {
        ProductDetailResponse.ProductParams params = new ProductDetailResponse.ProductParams();
        params.setModel(product.getModel());
        params.setOs(product.getOs());
        params.setPositioning(product.getPositioning());
        params.setCpuModel(product.getCpuModel());
        params.setCpuSeries(product.getCpuSeries());
        params.setMaxTurboFreq(product.getMaxTurboFreq());
        params.setCpuChip(product.getCpuChip());
        params.setScreenSize(product.getScreenSize());
        params.setScreenRatio(product.getScreenRatio());
        params.setResolution(product.getResolution());
        params.setColorGamut(product.getColorGamut());
        params.setRefreshRate(product.getRefreshRate());
        params.setRamType(product.getRamType());
        params.setSsdType(product.getSsdType());
        params.setGpuType(product.getGpuType());
        params.setVramType(product.getVramType());
        params.setCamera(product.getCamera());
        params.setWifi(product.getWifi());
        params.setBluetooth(product.getBluetooth());
        params.setDataInterfaces(product.getDataInterfaces());
        params.setVideoInterfaces(product.getVideoInterfaces());
        params.setAudioInterfaces(product.getAudioInterfaces());
        params.setKeyboard(product.getKeyboard());
        params.setFaceId(product.getFaceId());
        params.setWeight(product.getWeight());
        params.setThickness(product.getThickness());
        params.setSoftware(product.getSoftware());

        // 注意：ramCapacity字段需要从SKU中获取，这里使用一个默认值或从商品描述中提取
        params.setRamCapacity("32GB(16+16)"); // 默认值，实际业务中可能需要计算

        return params;
    }
}