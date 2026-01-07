package com.jingdong.mall.service;

import com.jingdong.mall.model.dto.request.SkuAddRequest;
import com.jingdong.mall.model.dto.request.SkuUpdateRequest;
import com.jingdong.mall.model.dto.response.SkuAddResponse;

public interface ProductSkuService {

    /**
     * 新增SKU
     * @param request 新增SKU请求参数
     * @return 新增SKU响应
     */
    SkuAddResponse addSku(SkuAddRequest request);

    /**
     * 更新SKU
     * @param skuId SKU ID
     * @param request 更新SKU请求参数
     */
    void updateSku(Integer skuId, SkuUpdateRequest request);

    /**
     * 删除SKU
     * @param skuId SKU ID
     */
    void deleteSku(Integer skuId);
}