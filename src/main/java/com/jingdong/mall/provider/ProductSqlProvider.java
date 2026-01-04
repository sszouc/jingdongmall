package com.jingdong.mall.provider;

import com.jingdong.mall.model.dto.request.ProductListRequest;
import com.jingdong.mall.model.entity.Product;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.jdbc.SQL;

/**
 * 商品动态SQL提供者
 */
public class ProductSqlProvider {

    /**
     * 构建商品列表查询SQL
     */
    public String selectProductList(ProductListRequest request) {
        SQL sql = new SQL();

        // 使用反引号避免关键字冲突
        sql.SELECT("p.*");
        sql.SELECT("(SELECT MIN(price) FROM product_sku WHERE product_id = p.id AND is_active = 1) as min_price");
        sql.FROM("product p");
        sql.WHERE("p.is_active = 1");

        // 关键词搜索条件
        if (request.getKeyword() != null && !request.getKeyword().trim().isEmpty()) {
            sql.WHERE("p.name LIKE CONCAT('%', #{request.keyword}, '%')");
        }

        // 分类筛选条件
        if (request.getCategoryId() != null) {
            sql.WHERE("p.category_id = #{request.categoryId}");
        }

        // 价格区间筛选
        if (request.getMinPrice() != null) {
            sql.WHERE("(SELECT MIN(price) FROM product_sku WHERE product_id = p.id AND is_active = 1) >= #{request.minPrice}");
        }

        if (request.getMaxPrice() != null) {
            sql.WHERE("(SELECT MIN(price) FROM product_sku WHERE product_id = p.id AND is_active = 1) <= #{request.maxPrice}");
        }

        // 排序方式
        String sort = request.getSort();
        if (sort != null) {
            switch (sort) {
                case "price_asc":
                    sql.ORDER_BY("min_price ASC");
                    break;
                case "price_desc":
                    sql.ORDER_BY("min_price DESC");
                    break;
                case "sales_desc":
                    // 假设有一个方法可以获取销量，这里简化处理
                    sql.ORDER_BY("(SELECT SUM(sales_count) FROM product_sku WHERE product_id = p.id) DESC");
                    break;
                default: // created_desc
                    sql.ORDER_BY("p.created_time DESC");
                    break;
            }
        } else {
            sql.ORDER_BY("p.created_time DESC");
        }

        // 分页
        if (request.getPageSize() != null && request.getPage() != null) {
            int offset = (request.getPage() - 1) * request.getPageSize();
            return sql.toString() + " LIMIT " + request.getPageSize() + " OFFSET " + offset;
        }

        return sql.toString();
    }

    /**
     * 构建商品总数统计SQL
     */
    public String countProductList(ProductListRequest request) {
        SQL sql = new SQL();
        sql.SELECT("COUNT(*)");
        sql.FROM("product p");
        sql.WHERE("p.is_active = 1");

        // 关键词搜索条件
        if (request.getKeyword() != null && !request.getKeyword().trim().isEmpty()) {
            sql.WHERE("p.name LIKE CONCAT('%', #{request.keyword}, '%')");
        }

        // 分类筛选条件
        if (request.getCategoryId() != null) {
            sql.WHERE("p.category_id = #{request.categoryId}");
        }

        // 价格区间筛选
        if (request.getMinPrice() != null) {
            sql.WHERE("(SELECT MIN(price) FROM product_sku WHERE product_id = p.id AND is_active = 1) >= #{request.minPrice}");
        }

        if (request.getMaxPrice() != null) {
            sql.WHERE("(SELECT MIN(price) FROM product_sku WHERE product_id = p.id AND is_active = 1) <= #{request.maxPrice}");
        }

        return sql.toString();
    }

    /**
     * 插入商品SQL
     */
    public String insertProduct(final Product product) {
        return new SQL() {{
            INSERT_INTO("product");

            if (product.getCategoryId() != null) {
                VALUES("category_id", "#{categoryId}");
            }

            VALUES("name", "#{name}");

            if (product.getDescription() != null) {
                VALUES("description", "#{description}");
            }

            if (product.getDetailHtml() != null) {
                VALUES("detail_html", "#{detailHtml}");
            }

            if (product.getMainImages() != null) {
                VALUES("main_images", "#{mainImages, jdbcType=VARCHAR}");
            }

            if (product.getTags() != null) {
                VALUES("tags", "#{tags, jdbcType=VARCHAR}");
            }

            // 商品参数字段
            if (product.getModel() != null) {
                VALUES("model", "#{model}");
            }

            if (product.getOs() != null) {
                VALUES("os", "#{os}");
            }

            if (product.getPositioning() != null) {
                VALUES("positioning", "#{positioning}");
            }

            if (product.getCpuModel() != null) {
                VALUES("cpu_model", "#{cpuModel}");
            }

            if (product.getCpuSeries() != null) {
                VALUES("cpu_series", "#{cpuSeries}");
            }

            if (product.getMaxTurboFreq() != null) {
                VALUES("max_turbo_freq", "#{maxTurboFreq}");
            }

            if (product.getCpuChip() != null) {
                VALUES("cpu_chip", "#{cpuChip}");
            }

            if (product.getScreenSize() != null) {
                VALUES("screen_size", "#{screenSize}");
            }

            if (product.getScreenRatio() != null) {
                VALUES("screen_ratio", "#{screenRatio}");
            }

            if (product.getResolution() != null) {
                VALUES("resolution", "#{resolution}");
            }

            if (product.getColorGamut() != null) {
                VALUES("color_gamut", "#{colorGamut}");
            }

            if (product.getRefreshRate() != null) {
                VALUES("refresh_rate", "#{refreshRate}");
            }

            if (product.getRamType() != null) {
                VALUES("ram_type", "#{ramType}");
            }

            if (product.getSsdType() != null) {
                VALUES("ssd_type", "#{ssdType}");
            }

            if (product.getGpuType() != null) {
                VALUES("gpu_type", "#{gpuType}");
            }

            if (product.getVramType() != null) {
                VALUES("vram_type", "#{vramType}");
            }

            if (product.getCamera() != null) {
                VALUES("camera", "#{camera}");
            }

            if (product.getWifi() != null) {
                VALUES("wifi", "#{wifi}");
            }

            if (product.getBluetooth() != null) {
                VALUES("bluetooth", "#{bluetooth}");
            }

            if (product.getDataInterfaces() != null) {
                VALUES("data_interfaces", "#{dataInterfaces}");
            }

            if (product.getVideoInterfaces() != null) {
                VALUES("video_interfaces", "#{videoInterfaces}");
            }

            if (product.getAudioInterfaces() != null) {
                VALUES("audio_interfaces", "#{audioInterfaces}");
            }

            if (product.getKeyboard() != null) {
                VALUES("keyboard", "#{keyboard}");
            }

            if (product.getFaceId() != null) {
                VALUES("face_id", "#{faceId}");
            }

            if (product.getWeight() != null) {
                VALUES("weight", "#{weight}");
            }

            if (product.getThickness() != null) {
                VALUES("thickness", "#{thickness}");
            }

            if (product.getSoftware() != null) {
                VALUES("software", "#{software}");
            }

            // 默认值
            VALUES("is_active", "1");
            VALUES("is_deleted", "0");
            VALUES("created_time", "NOW()");
            VALUES("updated_time", "NOW()");
        }}.toString();
    }
}