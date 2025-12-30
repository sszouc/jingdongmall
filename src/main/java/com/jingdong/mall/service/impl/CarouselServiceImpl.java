package com.jingdong.mall.service.impl;

import com.jingdong.mall.common.exception.BusinessException;
import com.jingdong.mall.common.exception.ErrorCode;
import com.jingdong.mall.mapper.CarouselMapper;
import com.jingdong.mall.model.dto.request.CarouselAddRequest;
import com.jingdong.mall.model.dto.response.CarouselAddResponse;
import com.jingdong.mall.model.dto.response.CarouselDeleteResponse;
import com.jingdong.mall.model.dto.response.CarouselResponse;
import com.jingdong.mall.model.entity.Carousel;
import com.jingdong.mall.service.CarouselService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
            throw new BusinessException(ErrorCode.CAROUSEL_GET_FAILED);
        }
    }

    // 新增方法实现
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CarouselAddResponse addCarousel(CarouselAddRequest request) {
        try {
            log.info("新增轮播图请求：{}", request);

            // 构建轮播图实体，设置默认值
            Carousel carousel = new Carousel();
            carousel.setImgUrl(request.getImgUrl());
            carousel.setLinkUrl(request.getLinkUrl());
            carousel.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
            carousel.setIsActive(request.getIsActive() != null ? request.getIsActive() : 1);

            // 执行新增
            int result = carouselMapper.insertCarousel(carousel);
            if (result <= 0) {
                throw new BusinessException(ErrorCode.CAROUSEL_GRATE_FAILED);
            }

            // 构建响应
            CarouselAddResponse response = new CarouselAddResponse();
            response.setId(carousel.getId());
            response.setImgUrl(carousel.getImgUrl());
            response.setLinkUrl(carousel.getLinkUrl());
            response.setSortOrder(carousel.getSortOrder());
            response.setIsActive(carousel.getIsActive());

            log.info("新增轮播图成功，ID：{}", carousel.getId());
            return response;

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("新增轮播图系统异常", e);
            throw new BusinessException(ErrorCode.CAROUSEL_GRATE_FAILED);
        }
    }

    @Override
    @Transactional
    public CarouselDeleteResponse deleteCarousel(Long id) {
        try {
            // 检查轮播图是否存在
            int exists = carouselMapper.existsById(id);
            if (exists <= 0) {
                throw new BusinessException(ErrorCode.CAROUSEL_NOT_EXIST);
            }

            // 执行删除
            int result = carouselMapper.deleteCarouselById(id);
            if (result <= 0) {
                throw new BusinessException(ErrorCode.CAROUSEL_DELETE_FAILED);
            }

            // 构建响应
            String deleteTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            log.info("轮播图删除成功，ID: {}", id);
            return new CarouselDeleteResponse(id, deleteTime);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("轮播图删除异常，ID: {}", id, e);
            throw new BusinessException(ErrorCode.CAROUSEL_DELETE_FAILED);
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