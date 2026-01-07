package com.jingdong.mall.service;

import com.jingdong.mall.model.dto.request.SkuAddRequest;
import com.jingdong.mall.model.dto.response.SkuAddResponse;

public interface ProductSkuService {

    /**
     * 新增SKU
     * @param request 新增SKU请求参数
     * @return 新增SKU响应
     */
    SkuAddResponse addSku(SkuAddRequest request);
}