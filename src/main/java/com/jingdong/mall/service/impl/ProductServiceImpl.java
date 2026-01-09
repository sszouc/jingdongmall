package com.jingdong.mall.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jingdong.mall.common.exception.BusinessException;
import com.jingdong.mall.common.exception.ErrorCode;
import com.jingdong.mall.mapper.ProductCategoryMapper;
import com.jingdong.mall.mapper.ProductMapper;
import com.jingdong.mall.mapper.ProductSkuMapper;
import com.jingdong.mall.model.dto.request.ProductAddRequest;
import com.jingdong.mall.model.dto.request.ProductListRequest;
import com.jingdong.mall.model.dto.request.ProductUpdateRequest;
import com.jingdong.mall.model.dto.request.SkuAddRequest;
import com.jingdong.mall.model.dto.response.*;
import com.jingdong.mall.model.entity.Product;
import com.jingdong.mall.model.entity.ProductSku;
import com.jingdong.mall.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Autowired
    private ProductCategoryMapper productCategoryMapper;

    @Override
    public ProductDetailResponse getProductDetail(Integer productId) {
        // 1. 验证商品ID
        if (productId == null || productId <= 0) {
            throw new BusinessException(ErrorCode.PRODUCT_ID_NULL);
        }

        // 2. 查询商品基本信息
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_EXIST);
        }

        // 3. 查询商品SKU列表
        List<ProductSku> skus = productSkuMapper.selectByProductId(productId);

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
            throw new BusinessException(ErrorCode.MIN_PRICE_ZERO);
        }
        if (request.getMaxPrice() != null && request.getMaxPrice().compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new BusinessException(ErrorCode.MAX_PRICE_ZERO);
        }
        if (request.getMinPrice() != null && request.getMaxPrice() != null
                && request.getMinPrice().compareTo(request.getMaxPrice()) > 0) {
            throw new BusinessException(ErrorCode.MAX_LOWER_MIN);
        }

        // 分类ID验证
        if (request.getCategoryId() != null && request.getCategoryId() <= 0) {
            throw new BusinessException(ErrorCode.CATEGORY_ID_INVALID);
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
                throw new BusinessException(ErrorCode.SORT_INVALID);
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductAddResponse addProduct(ProductAddRequest request) {
        try {
            log.info("开始新增商品，商品名称：{}", request.getName());

            // 1. 检查商品名称是否重复
            int duplicateCount = productMapper.countByName(request.getName());
            if (duplicateCount > 0) {
                log.warn("商品名称重复：{}", request.getName());
                throw new BusinessException(ErrorCode.PRODUCT_NAME_DUPLICATE);
            }

            // 2. 检查分类ID是否存在（如果提供了分类ID）
            if (request.getCategoryId() != null && request.getCategoryId() > 0) {
                int categoryExists = productCategoryMapper.countById(request.getCategoryId());
                if (categoryExists == 0) {
                    log.warn("分类ID不存在：{}", request.getCategoryId());
                    throw new BusinessException(ErrorCode.CATEGORY_NOT_EXIST);
                }
            }

            // 3. 转换JSON字段
            String mainImagesJson = null;
            String tagsJson = null;

            try {
                if (request.getMainImages() != null && !request.getMainImages().isEmpty()) {
                    mainImagesJson = objectMapper.writeValueAsString(request.getMainImages());
                }
                if (request.getTags() != null && !request.getTags().isEmpty()) {
                    tagsJson = objectMapper.writeValueAsString(request.getTags());
                }
            } catch (JsonProcessingException e) {
                log.error("JSON转换失败", e);
                throw new BusinessException(ErrorCode.PRODUCT_CREATE_FAILED, "商品图片或标签格式错误");
            }

            // 4. 构建Product实体
            Product product = new Product();
            product.setCategoryId(request.getCategoryId());
            product.setName(request.getName());
            product.setDescription(request.getDescription());
            product.setDetailHtml(request.getDetailHtml());
            product.setMainImages(mainImagesJson);
            product.setTags(tagsJson);
            product.setModel(request.getModel());
            product.setOs(request.getOs());
            product.setPositioning(request.getPositioning());
            product.setCpuModel(request.getCpuModel());
            product.setCpuSeries(request.getCpuSeries());
            product.setMaxTurboFreq(request.getMaxTurboFreq());
            product.setCpuChip(request.getCpuChip());
            product.setScreenSize(request.getScreenSize());
            product.setScreenRatio(request.getScreenRatio());
            product.setResolution(request.getResolution());
            product.setColorGamut(request.getColorGamut());
            product.setRefreshRate(request.getRefreshRate());
            product.setRamType(request.getRamType());
            product.setSsdType(request.getSsdType());
            product.setGpuType(request.getGpuType());
            product.setVramType(request.getVramType());
            product.setCamera(request.getCamera());
            product.setWifi(request.getWifi());
            product.setBluetooth(request.getBluetooth());
            product.setDataInterfaces(request.getDataInterfaces());
            product.setVideoInterfaces(request.getVideoInterfaces());
            product.setAudioInterfaces(request.getAudioInterfaces());
            product.setKeyboard(request.getKeyboard());
            product.setFaceId(request.getFaceId());
            product.setWeight(request.getWeight());
            product.setThickness(request.getThickness());
            product.setSoftware(request.getSoftware());
            product.setIsActive(1);
            product.setIsDeleted(0);

            // 5. 插入数据库
            int result = productMapper.insert(product);

            if (result == 0) {
                log.error("插入商品失败，商品名称：{}", request.getName());
                throw new BusinessException(ErrorCode.PRODUCT_CREATE_FAILED, "数据库插入失败");
            }

            log.info("商品新增成功，商品ID：{}，商品名称：{}", product.getId(), product.getName());

            // 6. 返回响应
            ProductAddResponse response = new ProductAddResponse();
            response.setId(product.getId());

            return response;

        } catch (BusinessException e) {
            log.warn("新增商品业务异常：{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("新增商品系统异常", e);
            throw new BusinessException(ErrorCode.PRODUCT_CREATE_FAILED, "系统异常，请稍后重试");
        }
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
        response.setCategoryId(product.getCategoryId());

        response.setCategoryName(productMapper.getCategoryNameById(product.getCategoryId()));

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

        ProductSku productSku = productMapper.selectFirstSkuByProductId(product.getId());

        if (productSku != null && productSku.getRam() != null) {
            params.setRamCapacity(productSku.getRam());
        } else {
            params.setRamCapacity(null);
        }

        return params;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProduct(Integer id, ProductUpdateRequest request) {
        try {
            log.info("开始更新商品，id={}, request={}", id, request);

            // 基础校验（ID 必须由框架保证为整数，但业务上不能为空）
            if (id == null || id <= 0) {
                throw new BusinessException(ErrorCode.PRODUCT_ID_NULL);
            }

            // 检查商品是否存在
            int exists = productMapper.existsById(id);
            if (exists == 0) {
                log.warn("更新失败，商品不存在：id={}", id);
                throw new BusinessException(ErrorCode.PRODUCT_NOT_EXIST);
            }

            // 名称重复校验（只在request提供name时校验）
            if (request.getName() != null && !request.getName().trim().isEmpty()) {
                int nameCount = productMapper.countByNameExceptId(request.getName(), id);
                if (nameCount > 0) {
                    log.warn("商品名称被占用：name={}，id={}", request.getName(), id);
                    throw new BusinessException(ErrorCode.PRODUCT_NAME_DUPLICATE);
                }
            }

            // 分类验证（如果提供）
            if (request.getCategoryId() != null && request.getCategoryId() > 0) {
                int catExists = productCategoryMapper.countById(request.getCategoryId());
                if (catExists == 0) {
                    log.warn("更新失败，分类不存在：categoryId={}", request.getCategoryId());
                    throw new BusinessException(ErrorCode.CATEGORY_NOT_EXIST);
                }
            }

            // JSON 字段转换
            String mainImagesJson = null;
            String tagsJson = null;
            try {
                if (request.getMainImages() != null) {
                    mainImagesJson = objectMapper.writeValueAsString(request.getMainImages());
                }
                if (request.getTags() != null) {
                    tagsJson = objectMapper.writeValueAsString(request.getTags());
                }
            } catch (JsonProcessingException e) {
                log.error("更新商品时JSON转换失败", e);
                throw new BusinessException(ErrorCode.PRODUCT_UPDATE_FAILED, "图片或标签格式错误");
            }

            // 构建Product实体并仅设置需要更新的字段
            Product product = new Product();
            product.setId(id);
            product.setCategoryId(request.getCategoryId());
            product.setName(request.getName());
            product.setDescription(request.getDescription());
            product.setDetailHtml(request.getDetailHtml());
            product.setMainImages(mainImagesJson);
            product.setTags(tagsJson);
            product.setModel(request.getModel());
            product.setOs(request.getOs());
            product.setPositioning(request.getPositioning());
            product.setCpuModel(request.getCpuModel());
            product.setCpuSeries(request.getCpuSeries());
            product.setMaxTurboFreq(request.getMaxTurboFreq());
            product.setCpuChip(request.getCpuChip());
            product.setScreenSize(request.getScreenSize());
            product.setScreenRatio(request.getScreenRatio());
            product.setResolution(request.getResolution());
            product.setColorGamut(request.getColorGamut());
            product.setRefreshRate(request.getRefreshRate());
            product.setRamType(request.getRamType());
            product.setSsdType(request.getSsdType());
            product.setGpuType(request.getGpuType());
            product.setVramType(request.getVramType());
            product.setCamera(request.getCamera());
            product.setWifi(request.getWifi());
            product.setBluetooth(request.getBluetooth());
            product.setDataInterfaces(request.getDataInterfaces());
            product.setVideoInterfaces(request.getVideoInterfaces());
            product.setAudioInterfaces(request.getAudioInterfaces());
            product.setKeyboard(request.getKeyboard());
            product.setFaceId(request.getFaceId());
            product.setWeight(request.getWeight());
            product.setThickness(request.getThickness());
            product.setSoftware(request.getSoftware());

            // 执行更新
            int updateCount = productMapper.updateProduct(product);
            if (updateCount <= 0) {
                log.error("更新商品失败，id={}", id);
                throw new BusinessException(ErrorCode.PRODUCT_UPDATE_FAILED, "数据库更新未生效");
            }

            log.info("更新商品成功，id={}", id);

        } catch (BusinessException e) {
            log.warn("更新商品业务异常：{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("更新商品系统异常", e);
            throw new BusinessException(ErrorCode.PRODUCT_UPDATE_FAILED, "系统异常，请稍后重试");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProduct(Integer id) {
        try {
            log.info("开始删除商品，id={}", id);

            if (id == null || id <= 0) {
                throw new BusinessException(ErrorCode.PRODUCT_ID_NULL);
            }

            // 检查商品是否存在
            int exist = productMapper.existsById(id);
            if (exist == 0) {
                log.warn("删除失败，商品不存在：id={}", id);
                throw new BusinessException(ErrorCode.PRODUCT_NOT_EXIST);
            }

            // 执行删除（直接DELETE，product_sku 有外键 ON DELETE CASCADE）
            int rows = productMapper.deleteById(id);
            if (rows <= 0) {
                log.error("删除商品失败，id={}", id);
                throw new BusinessException(ErrorCode.PRODUCT_DELETE_FAILED, "删除商品失败或商品不存在");
            }

            log.info("删除商品成功，id={}", id);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除商品系统异常，id={}", id, e);
            throw new BusinessException(ErrorCode.PRODUCT_DELETE_FAILED, "删除商品失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateStatus(java.util.List<Integer> ids, Integer status) {
        try {
            log.info("开始批量更新商品上下架状态，ids={}, status={}", ids, status);

            // 基本校验
            if (ids == null || ids.isEmpty()) {
                throw new BusinessException(ErrorCode.PRODUCT_UPDATE_FAILED, "商品ID列表不能为空");
            }
            if (status == null || (status != 0 && status != 1)) {
                throw new BusinessException(ErrorCode.PRODUCT_UPDATE_FAILED, "状态参数不合法");
            }

            // 检查所有ID是否存在
            int existCount = productMapper.countByIds(ids);
            if (existCount != ids.size()) {
                log.warn("部分商品ID不存在或不可用，existCount={}, requestCount={}", existCount, ids.size());
                throw new BusinessException(ErrorCode.PRODUCT_NOT_EXIST, "部分商品不存在");
            }

            // 执行批量更新
            int updated = productMapper.batchUpdateStatus(ids, status);
            if (updated <= 0) {
                log.error("批量更新商品状态失败，ids={}", ids);
                throw new BusinessException(ErrorCode.PRODUCT_UPDATE_FAILED, "批量更新失败");
            }

            log.info("批量更新商品上下架状态成功，更新数={}", updated);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("批量更新商品状态系统异常", e);
            throw new BusinessException(ErrorCode.PRODUCT_UPDATE_FAILED, "批量更新商品状态失败");
        }
    }
}
