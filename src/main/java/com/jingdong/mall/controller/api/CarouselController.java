package com.jingdong.mall.controller.api;

import com.jingdong.mall.common.response.Result;
import com.jingdong.mall.model.dto.response.CarouselResponse;
import com.jingdong.mall.service.CarouselService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/product/carousel")
@Tag(name = "管理员/轮播图", description = "轮播图相关接口")
public class CarouselController {

    @Autowired
    private CarouselService carouselService;

    @Operation(
            summary = "获取轮播图",
            description = "获取所有启用的轮播图，按排序字段升序排列"
    )
    @GetMapping
    public Result<List<CarouselResponse>> getCarouselList() {
        List<CarouselResponse> carouselList = carouselService.getActiveCarousels();
        return Result.success("轮播图获取成功", carouselList);
    }
}