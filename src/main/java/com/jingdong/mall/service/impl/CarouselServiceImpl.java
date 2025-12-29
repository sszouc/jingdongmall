package com.jingdong.mall.service.impl;

import com.jingdong.mall.common.exception.BusinessException;
import com.jingdong.mall.common.exception.ErrorCode;
import com.jingdong.mall.mapper.CarouselMapper;
import com.jingdong.mall.model.dto.response.CarouselResponse;
import com.jingdong.mall.model.entity.Carousel;
import com.jingdong.mall.service.CarouselService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CarouselServiceImpl implements CarouselService {

    @Autowired
    private CarouselMapper carouselMapper;

    @Override
    public List<CarouselResponse> getActiveCarousels() {
        try {
            log.info("开始查询启用的轮播图");
            // 查询数据库中启用的轮播图
            List<Carousel> carouselList = carouselMapper.selectActiveCarousels();

            // 实体类转换为响应DTO
            List<CarouselResponse> responseList = carouselList.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());

            log.info("轮播图查询成功，共{}条数据", responseList.size());
            return responseList;
        } catch (Exception e) {
            log.error("查询轮播图失败", e);
            throw new BusinessException(ErrorCode.PRODUCT_DETAIL_ERROR, "获取轮播图失败");
        }
    }

    /**
     * 实体类转换为响应DTO
     */
    private CarouselResponse convertToResponse(Carousel carousel) {
        CarouselResponse response = new CarouselResponse();
        response.setId(carousel.getId());
        response.setImgUrl(carousel.getImgUrl());
        response.setLinkUrl(carousel.getLinkUrl());
        response.setSortOrder(carousel.getSortOrder());
        response.setIsActive(carousel.getIsActive());
        return response;
    }
}