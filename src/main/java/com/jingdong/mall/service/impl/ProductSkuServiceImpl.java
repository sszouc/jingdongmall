// src/main/java/com/jingdong/mall/service/impl/ProductSkuServiceImpl.java
package com.jingdong.mall.service.impl;

import com.jingdong.mall.common.exception.BusinessException;
import com.jingdong.mall.common.exception.ErrorCode;
import com.jingdong.mall.mapper.ProductSkuMapper;
import com.jingdong.mall.model.dto.request.SkuAddRequest;
import com.jingdong.mall.model.dto.request.SkuUpdateRequest;
import com.jingdong.mall.model.dto.request.SkuBatchStatusRequest;
import com.jingdong.mall.model.dto.response.SkuAddResponse;
import com.jingdong.mall.model.entity.ProductSku;
import com.jingdong.mall.service.ProductSkuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class ProductSkuServiceImpl implements ProductSkuService {

    @Autowired
    private ProductSkuMapper productSkuMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SkuAddResponse addSku(SkuAddRequest request) {
        try {
            log.info("开始新增SKU，产品ID：{}，规格：{}", request.getProductId(), request.getOs());

            // 1. 检查产品是否存在
            validateProductExists(request.getProductId());

            // 2. 检查相同规格的SKU是否已存在
            checkDuplicateSku(null, request.getProductId(), request.getOs(), request.getCpu(),
                    request.getRam(), request.getStorage(), request.getGpu());

            // 3. 构建ProductSku实体
            ProductSku sku = buildProductSkuForAdd(request);

            // 4. 插入数据库
            int result = productSkuMapper.insert(sku);

            if (result == 0) {
                log.error("插入SKU失败，产品ID：{}", request.getProductId());
                throw new BusinessException(ErrorCode.SKU_CREATE_FAILED, "数据库插入失败");
            }

            log.info("SKU新增成功，SKU ID：{}，产品ID：{}", sku.getId(), sku.getProductId());

            // 5. 返回响应
            return new SkuAddResponse(sku.getId());

        } catch (BusinessException e) {
            log.warn("新增SKU业务异常：{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("新增SKU系统异常", e);
            throw new BusinessException(ErrorCode.SKU_CREATE_FAILED, "系统异常，请稍后重试");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSku(Integer skuId, SkuUpdateRequest request) {
        try {
            log.info("开始更新SKU，SKU ID：{}", skuId);

            // 1. 验证SKU ID
            if (skuId == null || skuId <= 0) {
                throw new BusinessException(ErrorCode.SKU_INVALID, "SKU ID不能为空或小于等于0");
            }

            // 2. 检查SKU是否存在
            validateSkuExists(skuId);

            // 3. 获取现有SKU信息
            ProductSku existingSku = productSkuMapper.selectBySkuId(skuId);
            if (existingSku == null) {
                throw new BusinessException(ErrorCode.SKU_NOT_EXIST);
            }

            // 4. 如果更新了产品ID，检查新产品是否存在
            Integer productId = request.getProductId() != null ? request.getProductId() : existingSku.getProductId();
            if (request.getProductId() != null) {
                validateProductExists(request.getProductId());
            }

            // 5. 检查相同规格的SKU是否已存在（排除自身）
            String os = request.getOs() != null ? request.getOs() : existingSku.getOs();
            String cpu = request.getCpu() != null ? request.getCpu() : existingSku.getCpu();
            String ram = request.getRam() != null ? request.getRam() : existingSku.getRam();
            String storage = request.getStorage() != null ? request.getStorage() : existingSku.getStorage();
            String gpu = request.getGpu() != null ? request.getGpu() : existingSku.getGpu();

            checkDuplicateSku(skuId, productId, os, cpu, ram, storage, gpu);

            // 6. 构建更新的ProductSku实体
            ProductSku sku = buildProductSkuForUpdate(skuId, request, existingSku);

            // 7. 更新数据库
            int result = productSkuMapper.update(sku);

            if (result == 0) {
                log.error("更新SKU失败，SKU ID：{}", skuId);
                throw new BusinessException(ErrorCode.SKU_UPDATE_FAILED, "数据库更新失败");
            }

            log.info("SKU更新成功，SKU ID：{}", skuId);

        } catch (BusinessException e) {
            log.warn("更新SKU业务异常：{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("更新SKU系统异常", e);
            throw new BusinessException(ErrorCode.SKU_UPDATE_FAILED, "系统异常，请稍后重试");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSku(Integer skuId) {
        try {
            log.info("开始删除SKU，SKU ID：{}", skuId);

            // 1. 验证SKU ID
            if (skuId == null || skuId <= 0) {
                throw new BusinessException(ErrorCode.SKU_INVALID, "SKU ID不能为空或小于等于0");
            }

            // 2. 检查SKU是否存在
            validateSkuExists(skuId);

            // 3. 删除SKU
            int result = productSkuMapper.delete(skuId);

            if (result == 0) {
                log.error("删除SKU失败，SKU ID：{}", skuId);
                throw new BusinessException(ErrorCode.SKU_DELETE_FAILED, "数据库删除失败");
            }

            log.info("SKU删除成功，SKU ID：{}", skuId);

        } catch (BusinessException e) {
            log.warn("删除SKU业务异常：{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("删除SKU系统异常", e);
            throw new BusinessException(ErrorCode.SKU_DELETE_FAILED, "系统异常，请稍后重试");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateStatus(SkuBatchStatusRequest request) {
        try {
            log.info("开始批量更新SKU状态，ids={}，isActive={}", request.getIds(), request.getIsActive());

            List<Integer> ids = request.getIds();

            // 由于参数校验已在DTO层通过注解进行，service层不重复校验具体数值，
            // 这里只校验数据库中是否存在这些sku id
            int existCount = productSkuMapper.countByIds(ids);
            if (existCount != ids.size()) {
                log.warn("部分SKU不存在，期望数量={}，实际存在={}", ids.size(), existCount);
                throw new BusinessException(ErrorCode.SKU_NOT_EXIST, "部分SKU ID在数据库中不存在或已被删除");
            }

            int updated = productSkuMapper.batchUpdateStatus(ids, request.getIsActive());
            if (updated == 0) {
                log.error("批量更新SKU状态失败，ids={}", ids);
                throw new BusinessException(ErrorCode.SKU_UPDATE_FAILED, "数据库未更新任何行");
            }

            log.info("批量更新SKU状态成功，ids={}，isActive={}，updated={}", ids, request.getIsActive(), updated);

        } catch (BusinessException e) {
            log.warn("批量更新SKU状态业务异常：{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("批量更新SKU状态系统异常", e);
            throw new BusinessException(ErrorCode.SKU_UPDATE_FAILED, "系统异常，请稍后重试");
        }
    }

    /**
     * 检查产品是否存在
     */
    private void validateProductExists(Integer productId) {
        int productExists = productSkuMapper.countProductExists(productId);
        if (productExists == 0) {
            log.warn("产品不存在：{}", productId);
            throw new BusinessException(ErrorCode.PRODUCT_NOT_EXIST);
        }
    }

    /**
     * 检查相同规格的SKU是否已存在
     */
    private void checkDuplicateSku(Integer excludeSkuId, Integer productId,
                                   String os, String cpu, String ram,
                                   String storage, String gpu) {
        int duplicateCount;
        if (excludeSkuId != null) {
            duplicateCount = productSkuMapper.countDuplicateSkuExcludeSelf(
                    excludeSkuId, productId, os, cpu, ram, storage, gpu);
        } else {
            // 新增时，使用原有的检查方法（需要适配，或者新增方法）
            // 这里简化处理，我们假设新增时也使用排除自身的方法，排除ID传0
            duplicateCount = productSkuMapper.countDuplicateSkuExcludeSelf(
                    0, productId, os, cpu, ram, storage, gpu);
        }

        if (duplicateCount > 0) {
            log.warn("相同规格的SKU已存在，产品ID：{}，规格组合：{}/{}/{}/{}/{}",
                    productId, os, cpu, ram, storage, gpu);
            throw new BusinessException(ErrorCode.SKU_DUPLICATE, "相同规格的SKU已存在");
        }
    }

    /**
     * 检查SKU是否存在
     */
    private void validateSkuExists(Integer skuId) {
        int skuExists = productSkuMapper.countById(skuId);
        if (skuExists == 0) {
            log.warn("SKU不存在：{}", skuId);
            throw new BusinessException(ErrorCode.SKU_NOT_EXIST);
        }
    }

    /**
     * 构建新增的ProductSku实体
     */
    private ProductSku buildProductSkuForAdd(SkuAddRequest request) {
        ProductSku sku = new ProductSku();
        sku.setProductId(request.getProductId());
        sku.setPrice(request.getPrice());
        sku.setStock(request.getStock());

        // 设置销量，默认为0
        if (request.getSalesCount() != null) {
            sku.setSalesCount(request.getSalesCount());
        } else {
            sku.setSalesCount(0);
        }

        sku.setOs(request.getOs());
        sku.setCpu(request.getCpu());
        sku.setRam(request.getRam());
        sku.setStorage(request.getStorage());
        sku.setGpu(request.getGpu());
        sku.setSsdCapacity(request.getSsdCapacity());
        sku.setGpuChip(request.getGpuChip());
        sku.setVramCapacity(request.getVramCapacity());

        // 设置激活状态，默认为1
        if (request.getIsActive() != null) {
            if (request.getIsActive() != 0 && request.getIsActive() != 1) {
                throw new BusinessException(ErrorCode.PRODUCT_PARAM_ERROR, "is_active只能为0或1");
            }
            sku.setIsActive(request.getIsActive());
        } else {
            sku.setIsActive(1);
        }

        return sku;
    }

    /**
     * 构建更新的ProductSku实体
     */
    private ProductSku buildProductSkuForUpdate(Integer skuId, SkuUpdateRequest request, ProductSku existingSku) {
        ProductSku sku = new ProductSku();
        sku.setId(skuId);

        // 只更新非空字段，否则使用原有值
        sku.setProductId(request.getProductId() != null ? request.getProductId() : existingSku.getProductId());
        sku.setPrice(request.getPrice() != null ? request.getPrice() : existingSku.getPrice());
        sku.setStock(request.getStock() != null ? request.getStock() : existingSku.getStock());
        sku.setSalesCount(request.getSalesCount() != null ? request.getSalesCount() : existingSku.getSalesCount());
        sku.setOs(request.getOs() != null ? request.getOs() : existingSku.getOs());
        sku.setCpu(request.getCpu() != null ? request.getCpu() : existingSku.getCpu());
        sku.setRam(request.getRam() != null ? request.getRam() : existingSku.getRam());
        sku.setStorage(request.getStorage() != null ? request.getStorage() : existingSku.getStorage());
        sku.setGpu(request.getGpu() != null ? request.getGpu() : existingSku.getGpu());
        sku.setSsdCapacity(request.getSsdCapacity() != null ? request.getSsdCapacity() : existingSku.getSsdCapacity());
        sku.setGpuChip(request.getGpuChip() != null ? request.getGpuChip() : existingSku.getGpuChip());
        sku.setVramCapacity(request.getVramCapacity() != null ? request.getVramCapacity() : existingSku.getVramCapacity());

        // 设置激活状态
        if (request.getIsActive() != null) {
            if (request.getIsActive() != 0 && request.getIsActive() != 1) {
                throw new BusinessException(ErrorCode.PRODUCT_PARAM_ERROR, "is_active只能为0或1");
            }
            sku.setIsActive(request.getIsActive());
        } else {
            sku.setIsActive(existingSku.getIsActive());
        }

        return sku;
    }
}

