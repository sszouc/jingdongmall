package com.jingdong.mall.provider;

import com.jingdong.mall.model.entity.Product;
import org.apache.ibatis.jdbc.SQL;

/**
 * 商品新增动态SQL提供者
 */
public class ProductAddSqlProvider {

    /**
     * 构建新增商品SQL
     */
    public String insertProduct(Product product) {
        return new SQL() {{
            INSERT_INTO("product");

            // 动态设置字段，仅包含非空值
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
                VALUES("main_images", "#{mainImages}");
            }
            if (product.getTags() != null) {
                VALUES("tags", "#{tags}");
            }
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
            VALUES("is_active", "1"); // 默认启用
            VALUES("created_time", "NOW()");
            VALUES("updated_time", "NOW()");
        }}.toString();
    }

    /**
     * 构建商品名称重复性检查SQL
     */
    public String countByName(String name) {
        return new SQL() {{
            SELECT("COUNT(*)");
            FROM("product");
            WHERE("name = #{name}");
            WHERE("is_active = 1"); // 只检查启用状态的商品
        }}.toString();
    }
}