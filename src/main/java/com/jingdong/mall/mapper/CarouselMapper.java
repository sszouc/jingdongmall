package com.jingdong.mall.mapper;

import com.jingdong.mall.model.entity.Carousel;
import com.jingdong.mall.provider.CarouselAddSqlProvider;
import com.jingdong.mall.provider.CarouselSqlProvider;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CarouselMapper {
    /**
     * 查询所有启用的轮播图（按排序字段升序）
     */
    @SelectProvider(type = CarouselSqlProvider.class, method = "selectActiveCarousels")
    List<Carousel> selectActiveCarousels();
    /**
     * 根据ID删除轮播图
     */
    @DeleteProvider(type = CarouselSqlProvider.class, method = "deleteCarouselById")
    int deleteCarouselById(Long id);

    /**
     * 检查轮播图是否存在
     */
    @SelectProvider(type = CarouselSqlProvider.class, method = "existsById")
    int existsById(Long id);

    @InsertProvider(type = CarouselAddSqlProvider.class, method = "insertCarousel")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertCarousel(Carousel carousel);
}