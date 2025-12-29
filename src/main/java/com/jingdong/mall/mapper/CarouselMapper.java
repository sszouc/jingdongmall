package com.jingdong.mall.mapper;

import com.jingdong.mall.model.entity.Carousel;
import com.jingdong.mall.provider.CarouselSqlProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface CarouselMapper {
    /**
     * 查询所有启用的轮播图（按排序字段升序）
     */
    @SelectProvider(type = CarouselSqlProvider.class, method = "selectActiveCarousels")
    List<Carousel> selectActiveCarousels();
}