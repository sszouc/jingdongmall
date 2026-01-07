package com.jingdong.mall.service.impl;

import com.jingdong.mall.common.exception.BusinessException;
import com.jingdong.mall.common.exception.ErrorCode;
import com.jingdong.mall.mapper.ProductSkuMapper;
import com.jingdong.mall.model.dto.request.SkuAddRequest;
import com.jingdong.mall.model.dto.response.SkuAddResponse;
import com.jingdong.mall.model.entity.ProductSku;
import com.jingdong.mall.service.ProductSkuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            int productExists = productSkuMapper.countProductExists(request.getProductId());
            if (productExists == 0) {
                log.warn("产品不存在：{}", request.getProductId());
                throw new BusinessException(ErrorCode.PRODUCT_NOT_EXIST);
            }

            // 2. 检查相同规格的SKU是否已存在
            int duplicateCount = productSkuMapper.countDuplicateSku(
                    request.getProductId(),
                    request.getOs(),
                    request.getCpu(),
                    request.getRam(),
                    request.getStorage(),
                    request.getGpu()
            );

            if (duplicateCount > 0) {
                log.warn("相同规格的SKU已存在，产品ID：{}，规格组合：{}/{}/{}/{}/{}",
                        request.getProductId(),
                        request.getOs(), request.getCpu(), request.getRam(),
                        request.getStorage(), request.getGpu());
                throw new BusinessException(ErrorCode.SKU_NOT_EXIST, "相同规格的SKU已存在");
            }

            // 3. 构建ProductSku实体
            ProductSku sku = buildProductSku(request);

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

    /**
     * 构建ProductSku实体
     */
    private ProductSku buildProductSku(SkuAddRequest request) {
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
}