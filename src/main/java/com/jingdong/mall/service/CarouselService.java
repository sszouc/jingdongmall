package com.jingdong.mall.service;

import com.jingdong.mall.model.dto.request.CarouselAddRequest;
import com.jingdong.mall.model.dto.response.CarouselAddResponse;
import com.jingdong.mall.model.dto.response.CarouselResponse;

import java.util.List;

/**
 * 轮播图服务接口
 */
public interface CarouselService {
    /**
     * 获取所有启用的轮播图（按排序字段升序）
     */
    List<CarouselResponse> getActiveCarousels();

    /**
     * 新增轮播图（新增方法）
     */
    CarouselAddResponse addCarousel(CarouselAddRequest request);
}