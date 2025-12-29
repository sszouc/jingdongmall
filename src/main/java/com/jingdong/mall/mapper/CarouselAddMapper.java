package com.jingdong.mall.mapper;

import com.jingdong.mall.model.entity.Carousel;
import com.jingdong.mall.provider.CarouselAddSqlProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

/**
 * 轮播图新增相关Mapper
 */
@Mapper
public interface CarouselAddMapper {

    /**
     * 新增轮播图
     */
    @InsertProvider(type = CarouselAddSqlProvider.class, method = "insertCarousel")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertCarousel(Carousel carousel);
}