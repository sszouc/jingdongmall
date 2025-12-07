package com.jingdong.mall.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jingdong.mall.common.exception.BusinessException;
import com.jingdong.mall.common.exception.ErrorCode;
import com.jingdong.mall.mapper.ProductMapper;
import com.jingdong.mall.mapper.ProductSkuMapper;
import com.jingdong.mall.model.dto.request.ProductListRequest;
import com.jingdong.mall.model.dto.response.ProductDetailResponse;
import com.jingdong.mall.model.dto.response.ProductListResponse;
import com.jingdong.mall.model.dto.response.ProductSimpleResponse;
import com.jingdong.mall.model.entity.Product;
import com.jingdong.mall.model.entity.ProductSku;
import com.jingdong.mall.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

    @Override
    public ProductListResponse getProductList(ProductListRequest request) {
        log.info("获取商品列表, 查询参数: {}", request);

        try {
            // 1. 参数校验
            validateProductListRequest(request);

            // 2. 查询商品列表
            List<Product> products = productMapper.selectProductList(request);

            // 3. 统计总数
            Long total = productMapper.countProductList(request);

            // 4. 转换为响应对象
            List<ProductSimpleResponse> productSimpleList = convertToProductSimpleList(products);

            // 5. 构建响应
            ProductListResponse response = new ProductListResponse();
            response.setProductSimple(productSimpleList);
            response.setTotal(total);
            response.setPage(request.getPage());
            // 将pageSize转换为字符串类型，符合OpenAPI规范
            response.setPageSize(String.valueOf(request.getPageSize()));

            log.info("商品列表查询成功, 总记录数: {}, 当前页记录数: {}", total, productSimpleList.size());
            return response;

        } catch (BusinessException e) {
            log.warn("获取商品列表业务异常: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("获取商品列表系统异常", e);
            throw new BusinessException("获取商品列表失败，请稍后重试");
        }
    }

    // 在 validateProductListRequest 方法中，修正正则表达式验证：
    /**
     * 验证商品列表查询请求参数
     */
    private void validateProductListRequest(ProductListRequest request) {
        // 页码和页数验证
        if (request.getPage() == null || request.getPage() <= 0) {
            request.setPage(1);
        }
        if (request.getPageSize() == null || request.getPageSize() <= 0) {
            request.setPageSize(10);
        }
        // 限制最大页数，防止查询过多数据
        if (request.getPageSize() > 100) {
            request.setPageSize(100);
        }

        // 价格验证
        if (request.getMinPrice() != null && request.getMinPrice().compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new BusinessException("最低价格不能小于0");
        }
        if (request.getMaxPrice() != null && request.getMaxPrice().compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new BusinessException("最高价格不能小于0");
        }
        if (request.getMinPrice() != null && request.getMaxPrice() != null
                && request.getMinPrice().compareTo(request.getMaxPrice()) > 0) {
            throw new BusinessException("最低价格不能大于最高价格");
        }

        // 分类ID验证
        if (request.getCategoryId() != null && request.getCategoryId() <= 0) {
            throw new BusinessException("分类ID不合法");
        }

        // 排序方式验证
        if (org.springframework.util.StringUtils.hasText(request.getSort())) {
            String[] validSorts = {"price_asc", "price_desc", "created_desc", "sales_desc"};
            boolean isValid = false;
            for (String validSort : validSorts) {
                if (validSort.equals(request.getSort())) {
                    isValid = true;
                    break;
                }
            }
            if (!isValid) {
                throw new BusinessException("排序方式不合法");
            }
        }
    }

    /**
     * 将Product列表转换为ProductSimpleResponse列表
     */
    private List<ProductSimpleResponse> convertToProductSimpleList(List<Product> products) {
        if (products == null || products.isEmpty()) {
            return new ArrayList<>();
        }

        return products.stream()
                .map(this::convertToProductSimpleResponse)
                .collect(Collectors.toList());
    }

    /**
     * 将单个Product转换为ProductSimpleResponse
     */
    private ProductSimpleResponse convertToProductSimpleResponse(Product product) {
        ProductSimpleResponse response = new ProductSimpleResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());

        // 设置价格：使用最低价格
        response.setPrice(product.getMinPrice());

        // 设置主图：取第一张图片
        response.setImage(extractFirstImage(product.getMainImages()));

        // 设置标签：取第一个标签
        response.setTag(extractFirstTag(product.getTags()));

        return response;
    }

    /**
     * 从JSON字符串中提取第一张图片
     */
    private String extractFirstImage(String mainImagesJson) {
        if (!StringUtils.hasText(mainImagesJson)) {
            return "https://example.com/default-product.jpg";
        }

        try {
            List<String> images = objectMapper.readValue(
                    mainImagesJson,
                    new TypeReference<List<String>>() {}
            );
            if (images != null && !images.isEmpty()) {
                return images.get(0);
            }
        } catch (Exception e) {
            log.warn("解析商品主图JSON失败: {}", mainImagesJson, e);
        }

        return "https://example.com/default-product.jpg";
    }

    /**
     * 从JSON字符串中提取第一个标签
     */
    private String extractFirstTag(String tagsJson) {
        if (!StringUtils.hasText(tagsJson)) {
            return "商品";
        }

        try {
            List<String> tags = objectMapper.readValue(
                    tagsJson,
                    new TypeReference<List<String>>() {}
            );
            if (tags != null && !tags.isEmpty()) {
                return tags.get(0);
            }
        } catch (Exception e) {
            log.warn("解析商品标签JSON失败: {}", tagsJson, e);
        }

        return "商品";
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