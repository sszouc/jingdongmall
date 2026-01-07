package com.jingdong.mall.provider;

import com.jingdong.mall.model.dto.request.ProductListRequest;
import com.jingdong.mall.model.entity.Product;
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

        // 使用JOIN代替子查询
        sql.SELECT("p.*");
        sql.SELECT("MIN(ps.price) as min_price");
        sql.SELECT("SUM(ps.sales_count) as total_sales");
        sql.FROM("product p");
        sql.LEFT_OUTER_JOIN("product_sku ps ON p.id = ps.product_id AND ps.is_active = 1");
        sql.WHERE("p.is_active = 1");

        // 关键词搜索条件
        if (request.getKeyword() != null && !request.getKeyword().trim().isEmpty()) {
            sql.WHERE("p.name LIKE CONCAT('%', #{request.keyword}, '%')");
        }

        // 分类筛选条件
        if (request.getCategoryId() != null) {
            sql.WHERE("p.category_id = #{request.categoryId}");
        }

        // 分组
        sql.GROUP_BY("p.id");

        // HAVING子句处理价格筛选（分组后才能使用聚合函数）
        if (request.getMinPrice() != null) {
            sql.HAVING("MIN(ps.price) >= #{request.minPrice}");
        }

        if (request.getMaxPrice() != null) {
            sql.HAVING("MIN(ps.price) <= #{request.maxPrice}");
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
                    sql.ORDER_BY("total_sales DESC");
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

    /**
     * 更新商品SQL
     */
    public String updateProduct(final Product product) {
        return new SQL() {{
            UPDATE("product");

            if (product.getCategoryId() != null) {
                SET("category_id = #{categoryId}");
            }

            if (product.getName() != null) {
                SET("name = #{name}");
            }

            if (product.getDescription() != null) {
                SET("description = #{description}");
            }

            if (product.getDetailHtml() != null) {
                SET("detail_html = #{detailHtml}");
            }

            if (product.getMainImages() != null) {
                SET("main_images = #{mainImages, jdbcType=VARCHAR}");
            }

            if (product.getTags() != null) {
                SET("tags = #{tags, jdbcType=VARCHAR}");
            }

            // 商品参数字段
            if (product.getModel() != null) {
                SET("model = #{model}");
            }

            if (product.getOs() != null) {
                SET("os = #{os}");
            }

            if (product.getPositioning() != null) {
                SET("positioning = #{positioning}");
            }

            if (product.getCpuModel() != null) {
                SET("cpu_model = #{cpuModel}");
            }

            if (product.getCpuSeries() != null) {
                SET("cpu_series = #{cpuSeries}");
            }

            if (product.getMaxTurboFreq() != null) {
                SET("max_turbo_freq = #{maxTurboFreq}");
            }

            if (product.getCpuChip() != null) {
                SET("cpu_chip = #{cpuChip}");
            }

            if (product.getScreenSize() != null) {
                SET("screen_size = #{screenSize}");
            }

            if (product.getScreenRatio() != null) {
                SET("screen_ratio = #{screenRatio}");
            }

            if (product.getResolution() != null) {
                SET("resolution = #{resolution}");
            }

            if (product.getColorGamut() != null) {
                SET("color_gamut = #{colorGamut}");
            }

            if (product.getRefreshRate() != null) {
                SET("refresh_rate = #{refreshRate}");
            }

            if (product.getRamType() != null) {
                SET("ram_type = #{ramType}");
            }

            if (product.getSsdType() != null) {
                SET("ssd_type = #{ssdType}");
            }

            if (product.getGpuType() != null) {
                SET("gpu_type = #{gpuType}");
            }

            if (product.getVramType() != null) {
                SET("vram_type = #{vramType}");
            }

            if (product.getCamera() != null) {
                SET("camera = #{camera}");
            }

            if (product.getWifi() != null) {
                SET("wifi = #{wifi}");
            }

            if (product.getBluetooth() != null) {
                SET("bluetooth = #{bluetooth}");
            }

            if (product.getDataInterfaces() != null) {
                SET("data_interfaces = #{dataInterfaces}");
            }

            if (product.getVideoInterfaces() != null) {
                SET("video_interfaces = #{videoInterfaces}");
            }

            if (product.getAudioInterfaces() != null) {
                SET("audio_interfaces = #{audioInterfaces}");
            }

            if (product.getKeyboard() != null) {
                SET("keyboard = #{keyboard}");
            }

            if (product.getFaceId() != null) {
                SET("face_id = #{faceId}");
            }

            if (product.getWeight() != null) {
                SET("weight = #{weight}");
            }

            if (product.getThickness() != null) {
                SET("thickness = #{thickness}");
            }

            if (product.getSoftware() != null) {
                SET("software = #{software}");
            }

            SET("updated_time = NOW()");

            WHERE("id = #{id}");
        }}.toString();
    }

    /**
     * 批量更新商品上下架状态
     * 使用IN列表
     */
    public String batchUpdateStatus(final java.util.Map<String, Object> params) {
        // params 包含 ids (List<Integer>) 和 status (Integer)
        Object idsObj = params.get("ids");
        Object statusObj = params.get("status");
        if (idsObj == null || statusObj == null) {
            return "";
        }

        @SuppressWarnings("unchecked")
        java.util.List<Integer> ids = (java.util.List<Integer>) idsObj;
        Integer status = (Integer) statusObj;

        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE product SET is_active = ").append(status).append(", updated_time = NOW() WHERE id IN (");
        for (int i = 0; i < ids.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append("#{ids[").append(i).append("]}");
        }
        sb.append(")");
        return sb.toString();
    }

    /**
     * 根据ID列表统计存在数量
     */
    public String countByIds(final java.util.Map<String, Object> params) {
        Object idsObj = params.get("ids");
        if (idsObj == null) return "SELECT 0";

        @SuppressWarnings("unchecked")
        java.util.List<Integer> ids = (java.util.List<Integer>) idsObj;
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT COUNT(*) FROM product WHERE id IN (");
        for (int i = 0; i < ids.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append("#{ids[").append(i).append("]}");
        }
        sb.append(") AND is_active IN (0,1)");
        return sb.toString();
    }
}

