package com.jingdong.mall.mapper;

import com.jingdong.mall.model.dto.request.ProductListRequest;
import com.jingdong.mall.model.entity.Product;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProductMapper {

    // 原有方法保持不变
    @Select("SELECT * FROM product WHERE id = #{id} AND is_active = 1")
    @Results({
            // 基础字段映射
            @Result(property = "id", column = "id"),
            @Result(property = "categoryId", column = "category_id"),
            @Result(property = "name", column = "name"),
            @Result(property = "description", column = "description"),
            @Result(property = "detailHtml", column = "detail_html"),
            @Result(property = "mainImages", column = "main_images"),
            @Result(property = "tags", column = "tags"),

            // CPU相关字段映射
            @Result(property = "model", column = "model"),
            @Result(property = "os", column = "os"),
            @Result(property = "positioning", column = "positioning"),
            @Result(property = "cpuModel", column = "cpu_model"),
            @Result(property = "cpuSeries", column = "cpu_series"),
            @Result(property = "maxTurboFreq", column = "max_turbo_freq"),
            @Result(property = "cpuChip", column = "cpu_chip"),

            // 屏幕相关字段映射
            @Result(property = "screenSize", column = "screen_size"),
            @Result(property = "screenRatio", column = "screen_ratio"),
            @Result(property = "resolution", column = "resolution"),
            @Result(property = "colorGamut", column = "color_gamut"),
            @Result(property = "refreshRate", column = "refresh_rate"),

            // 硬件相关字段映射
            @Result(property = "ramType", column = "ram_type"),
            @Result(property = "ssdType", column = "ssd_type"),
            @Result(property = "gpuType", column = "gpu_type"),
            @Result(property = "vramType", column = "vram_type"),

            // 功能相关字段映射
            @Result(property = "camera", column = "camera"),
            @Result(property = "wifi", column = "wifi"),
            @Result(property = "bluetooth", column = "bluetooth"),
            @Result(property = "dataInterfaces", column = "data_interfaces"),
            @Result(property = "videoInterfaces", column = "video_interfaces"),
            @Result(property = "audioInterfaces", column = "audio_interfaces"),
            @Result(property = "keyboard", column = "keyboard"),
            @Result(property = "faceId", column = "face_id"),

            // 物理属性字段映射
            @Result(property = "weight", column = "weight"),
            @Result(property = "thickness", column = "thickness"),
            @Result(property = "software", column = "software"),

            // 状态和时间字段映射
            @Result(property = "isActive", column = "is_active"),
            @Result(property = "createdTime", column = "created_time"),
            @Result(property = "updatedTime", column = "updated_time")
    })
    Product selectById(@Param("id") Integer id);

    @Select("SELECT COUNT(*) FROM product WHERE id = #{id} AND is_active = 1")
    int existsById(@Param("id") Integer id);

    /**
     * 分页查询商品列表
     * @param request 查询条件
     * @return 商品列表
     */
    @SelectProvider(type = ProductSqlProvider.class, method = "selectProductList")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "categoryId", column = "category_id"),
            @Result(property = "name", column = "name"),
            @Result(property = "description", column = "description"),
            @Result(property = "mainImages", column = "main_images"),
            @Result(property = "tags", column = "tags"),
            @Result(property = "createdTime", column = "created_time"),
            @Result(property = "updatedTime", column = "updated_time"),
            // 临时字段，用于获取最低价格
            @Result(property = "minPrice", column = "min_price")
    })
    List<Product> selectProductList(@Param("request") ProductListRequest request);

    /**
     * 统计符合条件的商品总数
     * @param request 查询条件
     * @return 商品总数
     */
    @SelectProvider(type = ProductSqlProvider.class, method = "countProductList")
    Long countProductList(@Param("request") ProductListRequest request);
}